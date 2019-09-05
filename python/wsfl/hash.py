from timeit import timeit
from multiprocessing import Pool
from concurrent import futures
import hashlib
import asyncio

data = None
with open('../../data/xyj.txt', 'rt') as fp:
    text = fp.read()
    data = text.encode()

N = 1000
def hash(_):
    m = hashlib.sha256()
    m.update(data)
    unuse = m.digest()

@timeit
def synchronize():
    for _ in range(N):
        hash(_)
    return N

@timeit
def process():
    with Pool(4) as p:
        p.map(hash, range(N))
    return N

@timeit
def thread():
    with futures.ThreadPoolExecutor(4) as p:
        p.map(hash, range(N))
    return N


@timeit
def asyncs():
    async def async_hash():
        hash(None)

    async def main():
        tasks = [async_hash() for i in range(N)]
        asyncio.gather(*tasks)

    asyncio.run(main(), debug=True)
    return N


synchronize()
process()
thread()
asyncs()


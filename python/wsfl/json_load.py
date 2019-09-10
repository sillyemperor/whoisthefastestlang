from timeit import timeit
from multiprocessing import Pool
from concurrent import futures
import ujson as json

big_json = None
with open('../../data/big.json', 'rt') as fp:
    big_json = fp.read()


def execute(_):
    json.loads(big_json)

N = 100000

@timeit
def synchronize():
    for _ in range(N):
        execute(_)
    return N

@timeit
def process():
    with Pool(4) as p:
        p.map(execute, range(N))
    return N


@timeit
def thread():
    with futures.ThreadPoolExecutor(4) as p:
        p.map(execute, range(N))
    return N

synchronize()
process()
thread()


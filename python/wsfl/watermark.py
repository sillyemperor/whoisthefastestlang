from PIL import Image, ImageDraw
import io
from timeit import timeit
from multiprocessing import Pool
from concurrent import futures

def watermark(_):
    front = Image.open('../../data/lena512color.jpg')
    stamp = Image.open('../../data/stamp.png')

    d = ImageDraw.Draw(front)
    d.bitmap((0,0), stamp.resize(front.size))
    bytes = io.BytesIO()
    front.save(bytes, 'JPEG')
    unuse = bytes.read()

N = 1000
@timeit
def synchronize():
    for _ in range(N):
        watermark(_)
    return N

@timeit
def process():
    with Pool(4) as p:
        p.map(watermark, range(N))
    return N

@timeit
def thread():
    with futures.ThreadPoolExecutor(4) as p:
        p.map(watermark, range(N))
    return N

synchronize()
process()
thread()
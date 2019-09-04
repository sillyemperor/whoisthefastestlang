import time

def timeit(f):
    def _wrapper():
        t = time.time()
        f()
        print('time', f.__name__, time.time() - t)
    return _wrapper
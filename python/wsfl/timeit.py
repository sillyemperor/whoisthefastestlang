import time

def timeit(f):
    def _wrapper():
        t = time.time()
        n = f()
        t = (time.time() - t)*1000

        print('time', f.__name__, t, '(ms)', t/n if n>0 else None, '(ms)')
    return _wrapper
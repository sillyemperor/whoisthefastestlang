# Who is the fastest lang
> ### 未特殊说明的情况下，所有的测试均在如下环境中进行：
> + 采用的机器
>   - 型号名称：	MacBook Pro
>   - 处理器名称：	Intel Core i5
>   - 处理器速度：	3.1 GHz
>   - 处理器数目：	1
>   - 核总数：	2
>   - 内存：	8 GB
>
> + 采用的Java版本：
>   - Java HotSpot(TM) 64-Bit Server VM (build 25.162-b12, mixed mode)
> + 采用的Python版本：
>   - Python 3.7.3


## 1. 并发访问网络连接。

假设我们需要访问许多网络连接并处理返回结果，可以不考虑访问顺序。最简单的方法就是一个个访问，优点是代码简单，缺点是速度太慢。要想提高速度就需要使用并发。下面就把几种语言各自的方案分别测试一下，看看效果。

下表数值为访问平均所需的**毫秒**数，[这里是测试数据](https://github.com/sillyemperor/whoisthefastestlang/blob/master/data/links.txt)

| 语言 | 顺序执行 | 多线程 | 多进程 | 异步 |
| --- | --- | --- | ---| --- |
| Java | 379 | 81 | 无 | 90 |
| Python | 1382 | 344 | 346 | 38 |

首先是Java，可以看见，多线程的效果还是不错的，我使用[parallelStream](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html#parallelStream--)方法来实现多线程调用。异步我对比了[HttpAsyncClient](https://hc.apache.org/httpcomponents-asyncclient-dev/index.html)和[Vertx Web Client](https://vertx.io/docs/vertx-web-client/java/)，最后选择了Vertx的执行结果。

Python的由于多线程饱受诟病，所以增加了多进程的方案，异步使用到了[aiohttp](https://github.com/aio-libs/aiohttp)。

经过几次执行，发现Python3的async效果非常好，将来计划引入nodejs，go和rust。
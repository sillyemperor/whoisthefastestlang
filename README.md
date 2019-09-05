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


## 1. 网页抓取

假设我们需要访问许多网络连接并处理返回结果，可以不考虑访问顺序。最简单的方法就是一个个访问，优点是代码简单，缺点是速度太慢。要想提高速度就需要使用并发。下面就把几种语言各自的方案分别测试一下，看看效果。

下表中的数值为访问平均所需的**毫秒**数，[这里是测试数据](https://github.com/sillyemperor/whoisthefastestlang/blob/master/data/links.txt)

| 语言 | 顺序执行 | 多线程 | 多进程 | 异步 |
| --- | --- | --- | ---| --- |
| Java | 379 | 81 | 无 | 90 |
| Python | 1382 | 344 | 346 | 38 |

首先是Java，可以看见，多线程的效果还是不错的，我使用[parallelStream](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html#parallelStream--)方法来实现多线程调用。异步我对比了[HttpAsyncClient](https://hc.apache.org/httpcomponents-asyncclient-dev/index.html)和[Vertx Web Client](https://vertx.io/docs/vertx-web-client/java/)，最后选择了Vertx的执行结果。

Python的由于多线程饱受诟病，所以增加了多进程的方案，异步使用到了[aiohttp](https://github.com/aio-libs/aiohttp)。

经过几次执行，发现Python3的async效果非常好，将来计划引入nodejs，go和rust。

## 2. Hash运算

访问网页更多是IO操作，CPU等待。Hash运算就不同了。我选择使用[SHA256](https://baike.baidu.com/item/sha256)进行测试。具体的做法就是多次对一段文本进行hash运算。

下表中的数值为1000次运算平均所需的**毫秒**数，[这里是测试数据](https://github.com/sillyemperor/whoisthefastestlang/blob/master/data/xyj.txt)

| 语言 | 顺序执行 | 多线程 | 多进程 | 异步 |
| --- | --- | --- | ---| --- |
| Java | 11 | 5 | 无 | 无 |
| Python | 4 | 2 | 2 | 5 |

很意外的Java居然比Python要慢一些，其实只要深入代码会发现，Python使用了[C代码](https://github.com/python/cpython/blob/master/Modules/clinic/sha256module.c.h)，而Java没有这样做。

## 3. 图片处理

我用给JPEG图片加水印来测试图片处理的能力。Python使用了[Pillow](https://python-pillow.org/)进行图像处理。Java使用的是JDK提供的JAI。

下表中的数值为1000次运算平均所需的**毫秒**数，[这里是底图](https://github.com/sillyemperor/whoisthefastestlang/blob/master/data/lena512color.jpg)，[这是用作水印的PNG图](https://github.com/sillyemperor/whoisthefastestlang/blob/master/data/stamp.png)

| 语言 | 顺序执行 | 多线程 | 多进程 | 异步 |
| --- | --- | --- | ---| --- |
| Java | 40 | 17 | 无 | 无 |
| Python | 43 | 18 | 22 | 无 |

可以看到这一次Java和Python的执行时间是相似的，Pillow使用了C来实现图像处理。Java的JAI框架默认安装的是纯Java实现的，但是可以在[官网](https://www.oracle.com/technetwork/java/install-jai-imageio-1-0-01-139659.html#PlatformRequirements)下载本地代码的实现，应该可以进一步提高性能。





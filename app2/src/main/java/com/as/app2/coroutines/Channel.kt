package com.`as`.app2.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

// 通道提供了一种在流中传输值的方法
// BlockingQueue 阻塞的put/take
// Channel 挂起的send/receive

fun main(args: Array<String>) {
//    channel1()
//    channel2()
//    channel3()
//    channel4()
//    channel5()
//    channel6()
    channel7()
}

fun channel7() {
    //计时器通道是一种特别的会合通道，每次经过特定的延迟都会从该通道进行消费并产生 Unit
    runBlocking {
        val tickerChannel = ticker(delayMillis = 100, initialDelayMillis = 0)//创建计时器通道
        var next = withTimeoutOrNull(1) { tickerChannel.receive() }
        println("1 $next")
        next = withTimeoutOrNull(50) { tickerChannel.receive() }
        println("2 $next")
        next = withTimeoutOrNull(60) { tickerChannel.receive() }
        println("3 $next")
        println("consumer....150ms")
        delay(150)
        next = withTimeoutOrNull(1) { tickerChannel.receive() }
        println("4 $next")
        next = withTimeoutOrNull(60) { tickerChannel.receive() }
        println("5 $next")
        tickerChannel.cancel()
    }
}

data class Ball(var hits: Int)

suspend fun player(name: String, table: Channel<Ball>) {
    for (ball in table) {
        ball.hits++
        println("$name $ball")
        delay(300) // 等待一段时间
        table.send(ball) // 将球发送到table管道
    }
}

fun channel6() {
    //通道的发送和接收操作是 公平的 调用它们的多个协程
    runBlocking {
        val table = Channel<Ball>() //扇入: 一个共享的 table（桌子）
        launch { player("ping", table) }//启动协程 发送到管道
        launch { player("pong", table) }
        table.send(Ball(0)) // 乒乓球
        delay(1000) // 延迟 1 秒钟
        coroutineContext.cancelChildren() // 游戏结束，取消它们
    }
}

fun channel5() {
    //Channel() 工厂函数与 produce 建造器通过一个可选的参数 capacity 来指定 缓冲区大小
    //像 BlockingQueue 容量一样，当缓冲区被占满的时候将会引起阻塞
    runBlocking {
        val channel = Channel<Int>(4)
        val launch = launch {//启动协程
            repeat(10) {
                println("Sending $it") // 在每一个元素发送前打印它们
                channel.send(it) // 将在缓冲区被占满时挂起
            }
        }
        delay(1000)
        launch.cancel()
    }
}


//不停发送
suspend fun sendString(channel: SendChannel<String>, s: String, time: Long) {
    while (true) {
        delay(time)
        channel.send(s)
    }
}

fun channel4() {
    //扇出:多个协程从相同的管道接收，在它们之间进行分布式工作。
//    runBlocking {
//        val numbers = produceNumbers2()//不停生产 往通道添加元素
//        repeat(5) { launchProcessor(it, numbers) }//构造5个协程,处理同一管道生产的元素
//        delay(950)
//        numbers.cancel()
//    }
    //扇入:多个协程可以发送到同一个通道
    runBlocking {
        val channel = Channel<String>()//多个协程都发送到此管道
        launch { sendString(channel, "111", 200) }//构建新协程,每200ms,不停发送到管道
        launch { sendString(channel, "222", 500) }//构建新协程,每500ms,不停发送到管道
        repeat(6) {
            println(channel.receive())
        }
        coroutineContext.cancelChildren()
    }
}

//构造协程 去处理
fun CoroutineScope.launchProcessor(id: Int, channel: ReceiveChannel<Int>) = launch {
    for (e in channel) {
        println("process $id receive $e")
    }
}

fun channel3() {
    //管道:是一种一个协程在流中开始生产可能无穷多个元素的模式
    runBlocking {
        val numbers = produceNumbers()//不停生产
        val squares = square(numbers)//不停再生产
        repeat(5) {
            println(squares.receive())//消费
        }
        println("Done")
        coroutineContext.cancelChildren()// 取消所有的子协程来让主协程结束
    }
}

fun channel2() {
    runBlocking {
        val squares = produceSquares()
        squares.consumeEach { println(it) }//2.2使用channel的consumeEach遍历消费生成出来的元素
        println("Done")
    }
}

fun channel1() {
    runBlocking {
        val channel = Channel<Int>()
        launch {
            for (x in 1..5) {
                channel.send(x * x)
            }
            channel.close()//1.和队列不同通道可以被关闭
        }
//        repeat(5) { println(channel.receive()) }
        for (y in channel) println(y)//使用 `for` 循环来打印所有被接收到的元素（直到通道被关闭）
        println("Done")
    }
}

//给CoroutineScope添加拓展函数produceSquares
fun CoroutineScope.produceSquares(): ReceiveChannel<Int> =
    produce {//2.1使用produce协程构建器,作为channel内元素的生成者
        for (x in 1..5) {
            send(x * x)//生产者:不断往通道里添加元素
        }
    }


fun CoroutineScope.produceNumbers(): ReceiveChannel<Int> =
    produce {
        var x = 1
        while (true) {//不停的生产
            send(x++)
        }
    }

fun CoroutineScope.produceNumbers2(): ReceiveChannel<Int> =
    produce {
        var x = 1
        while (true) {//不停的生产
            send(x++)
            delay(100)
        }
    }

fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> =
    produce {
        for (x in numbers) send(x * x)
    }

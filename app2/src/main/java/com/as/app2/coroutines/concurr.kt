package com.`as`.app2.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

//协程可用多线程调度器（比如默认的 Dispatchers.Default）并发执行。
//并发就会存在 共享的可变状态 的问题

suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100
    val k = 1000
    //开n个协程执行k次acton动作
    measureTimeMillis {
        coroutineScope {
            repeat(n) {
                launch { repeat(k) { action() } }
            }
        }
    }
}

fun main(args: Array<String>) {
//    concurrent1()
//    concurrent2()
//    concurrent3()
//    concurrent4()
//    concurrent5()
    concurrent6()
}

// 计数器 Actor 的各种类型
sealed class CounterMsg
object IncCounter : CounterMsg() // 递增计数器的单向消息
class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg() // 携带回复的请求
//actor 协程构建器是一个双重的 produce 协程构建器。
//一个 actor 与它接收消息的通道相关联，而一个 producer 与它发送元素的通道相关联。
//actor 在高负载下比锁更有效，因为在这种情况下它总是有工作要做，而且根本不需要切换到不同的上下文。

// 这个函数启动一个新的计数器 actor
fun CoroutineScope.counterActor() = actor<CounterMsg> {
    var counter = 0 // actor 状态
    for (msg in channel) { // 即将到来消息的迭代器
        when (msg) {
            is IncCounter -> counter++
            is GetCounter -> msg.response.complete(counter)
        }
    }
}

fun concurrent6() {
    //actor协程构建器 是由协程|被限制的状态|与其它协程通信的通道,组合而成的一个实体。
    //简单的可以写成一个函数， 拥有复杂状态的 actor 更适合由类来表示(密封类比较适合)
    runBlocking {
        val counter = counterActor()
        withContext(Dispatchers.Default) {
            massiveRun { counter.send(IncCounter) }
        }
        // 发送一条消息以用来从一个 actor 中获取计数值
        val response = CompletableDeferred<Int>()
        counter.send(GetCounter(response))
        println("${response.await()}")
        counter.close()
    }
}

val mutex = Mutex()
var counter5 = 0
fun concurrent5() {
    //互斥:类似synchronized 或者 ReentrantLock
    //协程中叫 Mutex 具有 lock 和 unlock 方法, Mutex.lock() 是一个挂起函数，它不会阻塞线程。
    //withLock 扩展函数，可以更方便的替代 mutex.lock(); try { …… } finally { mutex.unlock() } 的模式
    //虽然是细粒度的,但是对于某些必须定期修改共享状态的场景合适
    runBlocking {
        withContext(Dispatchers.Default) {
            massiveRun { mutex.withLock { counter5++ } }
        }
        println("$counter5")
    }
}

val counterContext = newSingleThreadContext("UI")
var counter4 = 0
fun concurrent4() {
    //限制线程 ：通过使用一个单线程的上下文, 把特定共享状态的所有访问权都限制在单个线程中
    //如所有对UI状态的修改都局限于单个时间分发线程或主线程中

    //细粒度的线程限制,运行缓慢,每个增量操作都得使用 [withContext(counterContext)] 块
    //从多线程 Dispatchers.Default 上下文切换到定义的单线程上下文
//    runBlocking {
//        withContext(Dispatchers.Default){
//            massiveRun {
//                withContext(counterContext){// 细粒度:将每次自增限制在单线程上下文中
//                    counter4++
//                }
//            }
//        }
//        println("$counter4")
//    }

    //在实践中多粗粒度，线程限制是在大段代码中执行的， 即在单线程上下文中运行每个协程
    runBlocking {
        withContext(counterContext) {// 粗粒度:将每次自增限制在单线程上下文中
            massiveRun {
                counter4++
            }
        }
        println("$counter4")
    }
}

var counter3 = AtomicInteger()
fun concurrent3() {
    //使用线程安全（也称为同步的、 可线性化、原子）的数据结构，它为需要在共享状态上执行的相应操作提供所有必需的同步处理
    //适用于普通计数器、集合、队列和其他标准数据结构以及它们的基本操作。
    //它并不容易被扩展来应对复杂状态、或一些没有现成的线程安全实现的复杂操作。
    runBlocking {
        runBlocking {
            withContext(Dispatchers.Default) {//线程池
                massiveRun { counter3.incrementAndGet() }
            }
        }
        println("$counter3")
    }
}

@Volatile // 在 Kotlin 中 `volatile` 是一个注解
var counter2 = 0
fun concurrent2() {
    //volatile 变量保证可原子读取和写入变量，但在大量动作（在我们的示例中即“递增”操作）发生时并不具备原子性。
    runBlocking {
        withContext(Dispatchers.Default) {//线程池
            massiveRun { counter2++ }
        }
    }
    println("$counter2")
}

var counter1 = 0

//开100个协程,每个累加1000次,结果并不是100000
//因为100协程在多个线程中存在并发问题
private fun concurrent1() {
    runBlocking {
        withContext(Dispatchers.Default) {//线程池
            massiveRun { counter1++ }
        }
    }
    println("$counter1")
}

package com.`as`.app2.coroutines

import kotlinx.coroutines.*

/**
 * CoroutineContext 协程上下文
 * CoroutineDispatcher 协程调度器
 * 协程总是运行在一些以 CoroutineContext 类型为代表的上下文中，协程上下文是各种不同元素的集合。其中主元素是协程中的 Job
 */

fun main(args: Array<String>) {
//    testDispatcher()
//    testChild()
    testScope()
//    test()
//    local()
}


/**
 * 协程上下文包含一个协程调度器, 它确定了相关的协程在哪个线程或哪些线程上执行。
 * 所有的协程构建器诸如 launch 和 async 都接收一个可选的 CoroutineContext 参数，用来显式的为一个新协程或其它上下文元素指定一个调度器。
 * 当协程构建器不传参数时, 从启动它的 CoroutineScope 中承袭了上下文以及调度器
 * 当协程在 GlobalScope 中启动时，使用的是默认调度器。默认调度器使用共享的后台线程池。
 * newSingleThreadContext 为协程的运行启动了一个线程,当不需要时要用close关闭或可以被重用
 * Unconfined非受限调度器:启动子协程后挂起, 恢复线程中的协程. 是 runBlocking 协程的默认调度器,通常不使用
 */
fun testDispatcher() {
    runBlocking {
        launch { println("1:${Thread.currentThread()}") }// 运行在父协程的上下文中，即 runBlocking 主协程的上下文中
        launch(Dispatchers.Unconfined) { println("2:${Thread.currentThread()}") }
        launch(Dispatchers.Default) { println("3:${Thread.currentThread()}") }
        launch(newSingleThreadContext("MyOwnThread")) { println("4:${Thread.currentThread()}") }
    }
}

/**
 * 当一个协程被其它协程在 CoroutineScope 中启动的时候，
 * 它将通过 CoroutineScope.coroutineContext 来承袭上下文，
 * 并且这个新协程的 Job 将会成为父协程作业的 子 作业。当一个父协程被取消的时候，所有它的子协程也会被递归的取消。
 * 一个父协程总是等待所有的子协程执行结束 .
 * 当使用 GlobalScope 来启动一个协程时, 新协程没有父协程, 独立运作和启动它的作用域无关
 */
fun testChild() =
    runBlocking {
        // 启动一个协程来处理某种传入请求（request）
        val request = launch {
            // 孵化了两个子作业, 其中一个通过 GlobalScope 启动
            GlobalScope.launch {
                println("job1: I run in GlobalScope and execute independently!")
                delay(1000)
                println("job1: I am not affected by cancellation of the request")
            }
            // 另一个则承袭了父协程的上下文
            launch {
                delay(100)
                println("job2: I am a child of the request coroutine")
                delay(1000)
                println("job2: I will not execute this line if my parent request is cancelled")
            }
        }
        delay(500L)
        request.cancel()
        delay(3000L) // 延迟JVM 查看结果
    }

fun test() = runBlocking {
    //当构建协程时, 假如需要定义多个上下文元素,如指定调度器和名称, 用'+'组合上下文中的元素
    launch(Dispatchers.Default + CoroutineName("test")) { println("job:${Thread.currentThread()}") }
}

val threadLocal = ThreadLocal<String?>()

/**
 * kotlin 为ThreadLocal添加了扩展函数asContextElement
 * 用于把线程的局部数据,在协程中传递
 * 它创建了额外的上下文元素，来保留给定 ThreadLocal 的值，并在每次协程切换其上下文时恢复它。
 */
fun local() = runBlocking {
    threadLocal.set("main")
    println("SS ${Thread.currentThread()}:+${threadLocal.get()}")
    //Dispatchers.Default 在后台线程池中启动了一个新的协程，它工作在线程池中的不同线程中,但它仍然具有线程局部变量的值，
    val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
        println("1${Thread.currentThread()}:+${threadLocal.get()}")
        yield()
        println("2${Thread.currentThread()}:+${threadLocal.get()}")
    }
    job.join()
    println("EE ${Thread.currentThread()}:+${threadLocal.get()}")
}

class Activity {
    private val mainScope = MainScope()

    fun destroy() {
        mainScope.cancel()
    }

    fun doSomething() {
        // 在示例中启动了 10 个协程，且每个都工作了不同的时长
        repeat(10) { i ->
            mainScope.launch {
                delay((i + 1) * 200L) // 延迟 200 毫秒、400 毫秒、600 毫秒等等不同的时间
                println("Coroutine $i is done")
            }
        }
    }
}

/**
 * 所有的协程构建器都声明为在协程作用域之上的扩展。
 * 通过创建一个 CoroutineScope 实例来管理协程的生命周期，
 * android中CoroutineScope 可以通过 CoroutineScope() 创建了一个通用作用域
 * 或者通过MainScope() 工厂函数使用 Dispatchers.Main 作为默认调度器的 UI 应用程序 创建作用域
 */
fun testScope() = runBlocking {
    val activity = Activity()
    activity.doSomething() // 运行测试函数
    println("Launched coroutines")
    delay(500L) // 延迟半秒钟
    println("Destroying activity!")
    activity.destroy() // 取消所有的协程
    delay(1000) // 为了在视觉上确认它们没有工作
}

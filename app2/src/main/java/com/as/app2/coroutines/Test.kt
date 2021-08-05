package com.`as`.app2.coroutines

import kotlinx.coroutines.*

fun main(args: Array<String>) {
//runBlockingTest()
//    launchTest()

//    job()

//    light()
    deamon()
}

private fun job() {
    val job = GlobalScope.launch {
        delay(1000L)
        println("job")
    }
    val job2 = GlobalScope.launch {
        job.join()
        delay(1000L)
        println("job2")
    }
//    Thread.sleep(3000L)// 阻塞主线程 3 秒钟来保证 JVM 存活
    runBlocking {// 阻塞主线程 3 秒钟来保证 JVM 存活
        job.start()
        delay(3000L)
    }
}

//挂起:把不占用当前线程的执行权,会释放底层线程用于其他用途.挂起结束后,再来继续执行挂起的代码
//方式二:不阻塞当前线程下,开启协程
fun launchTest() {
//    launch1()
    launch2()
}

fun launch1() {
    println("start")
    GlobalScope.launch {//新协程的生命周期和整个应用程序的生命周期一致
        delay(1000)//不阻塞,先挂起,1秒后再过来执行
        println("launch")
    }
    println("end")
    Thread.sleep(3000L)// 阻塞主线程 3 秒钟来保证 JVM 存活

//    runBlocking {
//        delay(3000L)// 阻塞主线程 3 秒钟来保证 JVM 存活
//    }
}

/**
 * runBlocking 与 coroutineScope
 * 都会等待其协程体以及所有子协程结束
 * runBlocking 方法会阻塞当前线程来等待， 而 coroutineScope 只是挂起
 * runBlocking 是常规函数，而 coroutineScope 是挂起函数
 */
fun launch2() = runBlocking {
    launch {
        delay(200L)
        println("task from runBlocking")
    }
    coroutineScope {
        launch {
            delay(500L)
            println("task from nested launch")
        }
        delay(100L)
        println("task from coroutine scope")
    }
    println("Coroutine scope is over")
}

/*1.提取函数重构:把launch内的代码块提取到独立的函数中*/
fun f1() = runBlocking {
    launch { doWork() }
    println("end")
}

suspend fun doWork() {
    delay(1000L)
    println("Start")
}

/**
 * 2.协程很轻量
 * 启动了 10 万个协程，并且在 5 秒钟后，每个协程都输出一个点。
 */
fun light() = runBlocking {
    repeat(100_000) {
        launch {
            delay(200L)
            print(".")
        }
    }
}

fun deamon() = runBlocking {
    //3.全局协程像守护线程,
    //在 GlobalScope 中启动的活动协程并不会使进程保活
    //GlobalScope中不再有活动携程时结束
    GlobalScope.launch {
        repeat(1000) { i ->//1.3秒时 i=2已经开始
            println("$i")
            delay(500L)
        }
    }
    delay(1300L)//阻塞主线程 1.3 秒钟来保证 JVM 存活
}


/**
 * 方式一:阻塞线程,开启线程
 */
fun runBlockingTest() {
    println("start")
    runBlocking {
        delay(3000)
        println("runBlocking")
    }
    println("end")
}

//runBlocking 在单元测试里使用
//@Test
fun testSuspend() =
    runBlocking<Unit> {//作为用来启动顶层主协程的适配器,返回类型为Unit
        GlobalScope.launch {
            delay(1000L)
            println("launch")
        }
        println("end")
        delay(3000L)
    }

fun testSuspend2() = runBlocking { // this: CoroutineScope
    launch { // 在 runBlocking 作用域中启动一个新协程
        delay(1000L)
        println("launch")
    }
    println("end")
}



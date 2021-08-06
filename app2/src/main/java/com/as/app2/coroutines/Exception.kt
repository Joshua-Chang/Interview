package com.`as`.app2.coroutines

import kotlinx.coroutines.*

//被取消的协程会在挂起点抛出 CancellationException 并且它会被协程的机制所忽略
/**
 * 当用launch|actor构建的协程,作为根协程时:发生地异常为uncaught异常,
 * 当用async|produce构建的协程,作为根协程时:发生地异常为caught异常,需要在await|receive时显式地捕获处理
 * */
fun main(args: Array<String>) {
//    test1()
//    test2()
//    test3()
//    test4()
//    test5()
//    test6()
    test7()
}
//监督协程中的每一个子job,不能向上传递给父job,只能通过CoroutineExceptionHandler处理自身的异常
fun test7() {
    runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
        }
        supervisorScope {
            val child = launch(handler) {
                println("The child throws an exception")
                throw AssertionError()
            }
            println("The scope is completing")
        }
        println("The scope is completed")
    }
}

//用 supervisorScope 替代 coroutineScope 来实现监督scope
//job的取消只会向下传播,当job自身执行失败的时候将所有子job全部取消
//job自身也会在所有的子作业执行结束前等待
fun test6() {
    runBlocking {
        try {
            supervisorScope {
                val child = launch {
                    try {
                        println("The child is sleeping")
                        delay(Long.MAX_VALUE)
                    } finally {
                        println("The child is cancelled")
                    }
                }
                // 使用 yield 来给我们的子作业一个机会来执行打印
                yield()
                println("Throwing an exception from the scope")
                throw AssertionError()
            }
        } catch (e: AssertionError) {
            println("Caught an assertion error")
        }
    }
}

//监督job
//SupervisorJob 的取消只会向下传播
fun test5() {
    runBlocking {
        val supervisor = SupervisorJob()
        //添加监督上下文
        with(CoroutineScope(kotlin.coroutines.coroutineContext + supervisor)) {
            // 启动第一个子作业——这个示例将会忽略它的异常（不要在实践中这么做！）
            val first = launch(CoroutineExceptionHandler { _, _ -> }) {
                println("first fail")
                throw AssertionError()
            }
            val second = launch {
                first.join()
                println("2 ${first.isCancelled}")
                try {
                    delay(Long.MAX_VALUE)
                } finally {// 取消了监督,因此second被取消
                    println("cancel second")
                }
            }
            first.join()
            println("--")
            supervisor.cancel()
            second.join()
        }
    }
}

fun test4() {
    runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
        }
        val job = GlobalScope.launch(handler) {
            launch { // 第一个子协程
                try {
                    delay(Long.MAX_VALUE)
                } finally {

//                    withContext(NonCancellable) {//a.当父协程的所有子协程都结束后，发生的异常才会被父协程处理
//                        delay(100)
//                        println("first child ..")
//                    }

//                    throw IOException() //b.多个子协程因异常而失败时,只捕获第一个
                }
            }
            launch { // 第二个子协程
                delay(10)
                println("Second child ...")
                throw ArithmeticException()
            }
            delay(Long.MAX_VALUE)
        }
        job.join()
    }
}

/**
 * 协程内部使用 CancellationException 来进行取消，这个异常会被所有的处理者忽略，
 * 即使可以被 catch 捕获, 也仅仅被作为额外的调试信息。
 * 当一个协程使用 Job.cancel 取消的时候，它会被终止，但是它不会取消它的父协程。
 */
fun test3() {
    runBlocking {
        var job = launch {
            var child = launch {
                try {
                    delay(Long.MAX_VALUE)
                } finally {//cancel可以被捕获,但没什么意义
                    println("Child is cancelled")
                }
            }
            yield()
            println("cancelling child")
            child.cancel()
            child.join()
            yield()
            println("parent is not cancelled")
        }
        job.join()
    }
}

fun test2() {
    runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("handler $exception")
        }
        //uncaught异常:launch构造的作为根协程 发生的异常 可以用CoroutineExceptionHandler加入上下文来捕获
        val launch = GlobalScope.launch(handler) {
            throw AssertionError()
        }
        //caught异常:async构造的作为根协程 发生的异常 需要await处显式的try-catch处理
        val async = GlobalScope.async(handler) { throw ArithmeticException() }
        try {
            async.await()
        } catch (e: Exception) {
            println("e:$e")
        }
    }
}

private fun test1() {
    runBlocking {//Dispatchers.Unconfined非受限调度器:启动子协程后挂起, 恢复线程中的协程
        val launch = GlobalScope.launch {//Dispatchers.Default默认调度器使用共享的后台线程池
            println("launch exception")
            throw IndexOutOfBoundsException()
        }
        launch.join()
        val async = GlobalScope.async {
            println("async exception")
            throw ArithmeticException()
        }
        try {
            async.await()
            println("un")
        } catch (e: ArithmeticException) {
            println("E:$e")
        }
    }
}
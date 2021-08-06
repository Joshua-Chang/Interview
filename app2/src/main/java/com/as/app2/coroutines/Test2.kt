package com.`as`.app2.coroutines

import kotlinx.coroutines.*

/**
 * 官方文档二:取消与超时
 */

//fun main() = runBlocking {
//    val job = launch {
//        repeat(1000) { i ->
//            println("$i")
//            delay(500L)
//        }
//    }
//    delay(1300L)
//    job.cancel()
//    job.join()
//    println("main quit")
//}

fun main(args: Array<String>) {

//    testCancel()
//    testCancel2()
    testFinally()
}

/**
 * try {……} finally {……} 表达式以及 Kotlin 的 use 函数一般在协程被取消的时候执行它们的终结动作,如释放资源动作
 * 处理取消时抛出的CancellationException
 */
fun testFinally() {
    runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                println("job: I'm running finally")
            }
        }
        delay(1300L) // 延迟一段时间
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消该作业并且等待它结束
        println("main: Now I can quit.")
    }
}

/**
 * 如果协程正在执行计算任务,执行完才取消
 * 取消时会抛出 CancellationException。
 */
private fun testCancel() {
    runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 5) { // 一个执行计算的循环，只是为了占用 CPU
                // 每秒打印消息两次
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // 等待一段时间
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消一个作业并且等待它结束
        println("main: Now I can quit.")
    }
}

/**
 * 取消正在计算的代码
 * 第一种方法是定期调用挂起函数来检查取消。对于这种目的 yield 是一个好的选择。
 * 另一种方法是显式的检查取消状态。isActive
 */
private fun testCancel2() {
    runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            /**
             * isActive 是一个可以被使用在 CoroutineScope 中的扩展属性
             */
            while (isActive) {// 可以被取消的计算循环
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }

        }
        delay(1300L) // 等待一段时间
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // 取消一个作业并且等待它结束
        println("main: Now I can quit.")
    }
}
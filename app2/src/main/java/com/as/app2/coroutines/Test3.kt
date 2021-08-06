package com.`as`.app2.coroutines

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // 假设我们在这里做了一些有用的事
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // 假设我们在这里也做了一些有用的事
    return 29
}

//组合挂起函数
fun main() {

    runBlocking<Unit> {

        //1. 默认顺序调用
        //使用场景:第一个函数的结果决定,是否需要调用或如何调用第二个函数

//        val time = measureTimeMillis {
//            val one = doSomethingUsefulOne()
//            val two = doSomethingUsefulTwo()
//            println("answer is ${one + two}")
//        }

        //2. 使用 async 并发
        /**
         * async 类似于 launch ,启动单独的协程,与其他协程一起并发工作
         * launch 返回 Job 对象, 不附带任何结果
         * async 返回 Deferred, 代表在稍后将会提供结果, 可以await 延期得到. 且是Job的子类可以取消等.
         */
//        val time = measureTimeMillis {
//            val one = async { doSomethingUsefulOne() }
//            val two = async { doSomethingUsefulTwo() }
//            println("The answer is ${one.await() + two.await()}")
//        }

        //2.1 惰性启动 async
        val time = measureTimeMillis {
            val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
            val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
            //主动调用start才会执行
            one.start()
            two.start()
            //加入不主动start, 这里还是顺序执行
            println("The answer is ${one.await() + two.await()}")
        }







        println("completed in $time ms")
    }

}

/**
 * 结构化async
 * async是CoroutineScope上的扩展, 可以将async写到coroutineScope函数的作用域内
 * 加入此时某协程有一个异常,作用域内所有协程都会被取消
 */
suspend fun concurrentSum(): Int = coroutineScope {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    one.await() + two.await()
}
package com.`as`.app2.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

val nums = (1..3).asFlow()
val strs = flowOf("one", "two", "three")
fun main(args: Array<String>) {
    runBlocking {

//        组合两个流中的元素
//        nums.zip(strs) { num, str -> "$num->$str" }.collect { println(it) }

//        用onEach 过渡操作符处理每个每次元素
//        发射接收速度不同步时,zip 按照接收速度
//        nums.onEach { delay(300) }.zip(strs.onEach { delay(400) }) { num, str -> "$num->$str" }.collect { println(it) }
//        combine按照发送速度组合
//        nums.onEach { delay(300) }.combine(strs.onEach { delay(400) }) { num, str -> "$num->$str" }.collect { println(it) }

//        flatMap展平流:表示异步接收的值序列,每个值都会触发对另一个值序列的请求

        //asFlow本身就是个流,其中的每个值都会触发requestFlow,其中又是一个流,因此可以展平操作
//        (1..3).asFlow().map { requestFlow(it) }

        val startTime = System.currentTimeMillis()
        //flatMapConcat上游发一个,下游处理一个
//        (1..3).asFlow().onEach { delay(100) }
//            .flatMapConcat { requestFlow(it) }
//            .collect {value -> println("$value cost ${System.currentTimeMillis()-startTime} ms") }
        //flatMapMerge 并发所有的传入流,并将其合并成单独的流
//        (1..3).asFlow().onEach { delay(100) }
//            .flatMapMerge { requestFlow(it) }
//            .collect {value -> println("$value cost ${System.currentTimeMillis()-startTime} ms") }
        //flatMapLatest只处理最新的
        (1..3).asFlow().onEach { delay(100) }
            .flatMapLatest { requestFlow(it) }
            .collect {value -> println("$value cost ${System.currentTimeMillis()-startTime} ms") }
    }
}
//第一个请求中触发另一个请求,如返回间隔 500 毫秒的两个字符串流的函数
fun requestFlow(i: Int): Flow<String> = flow {
    emit("$i:first request")
    delay(500)
    emit("$i:second request")
}
package com.`as`.app2.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

//过渡操作符应用于上游流，并返回下游流。 这些操作符也是冷操作符,操作符无本身不是挂起函数
fun main(args: Array<String>) {
    runBlocking {

        //map/filter过渡
        (1..5).asFlow().map { request ->
            delay(1000L)//可以挂起
            "string:$request"
        }.collect { result -> println(result) }

        //transform转换,重新发射
        (1..5).asFlow().transform { request ->
            emit("e"+request)
        }.collect { result -> println(result) }

        //take 取其中几个
        val flow = flow {
            try {
                emit(1)
                emit(2)
                println("xxxx")
                emit(3)
            } finally {
                println("finally")
            }
        }

        flow.take(2).collect { value -> println(value) }

        //末端操作符:是在流上用于启动流收集的挂起函数
        //除最常见的collect外还有
        //转化为各种集合，例如 toList 与 toSet。
        //获取第一个（first）值与确保流发射单个（single）值的操作符。
        //使用 reduce 与 fold 将流规约到单个值。
        val reduce =
            (1..5).asFlow().map { it * it }.reduce { accumulator, value -> accumulator + value }
        println(reduce)


    }


}

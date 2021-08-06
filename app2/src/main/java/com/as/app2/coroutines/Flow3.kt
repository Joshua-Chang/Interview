package com.`as`.app2.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

//上下文
fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

fun Flow3(): Flow<Int> = flow { // 流构建器
    for (i in 1..3) {
        delay(100L)
        emit(i)
    }
}

fun main(args: Array<String>) {
    runBlocking {
        var flow = Flow3()
        //flow { ... } 1.flow { ... }构建器中的代码不管细节,都运行在相应流的收集器提供的上下文中
        flow.collect { value -> log("$value") }
        //2.withContext 用于在 Kotlin 协程中改变代码的上下文,
        //但是 flow {...} 构建器中的代码必须遵循上下文保存属性，并且不允许从其他上下文中发射（emit）。
//        flow1.collect { value -> log("$value")}//异常

        //3.flowOn操作符,更改协程的上下文. 更改协程调度器,即创建另一个协程,在另一个线程里并发运行
        flow2.collect { value -> log("$value") }


        val measureTimeMillis = measureTimeMillis {
            flow.collect { value ->
                delay(400)//每100ms发送一个,每400ms接收一个
                println(value)
            }
        }//总计耗时(100+400)*3=1500ms
        println("collected cost:$measureTimeMillis ms")

        val measureTimeMillis2 = measureTimeMillis {
            flow.buffer().collect { value ->//压缩上游
                delay(400)//100ms只发送一次发送三个,每400ms接收一个
                println(value)
            }
        }//总计耗时100+400*3=1300ms
        println("collected cost:$measureTimeMillis2 ms")

        val measureTimeMillis3 = measureTimeMillis {
            flow.conflate().collect { value ->//跳过中间值,只处理最新的那个
                delay(400)//100ms发射1时,需要400ms接收. 接收期间右发射2,3 .只处理最新的3
                println(value)
            }
        }//总计耗时100+400+400=900ms
        println("collected cost:$measureTimeMillis3 ms")

        //末端操作符
        val measureTimeMillis4 = measureTimeMillis {
            flow.collectLatest  { value ->//只接收最新的那个
                delay(400)//100ms发射3次,只接收最新的3
                println(value)
            }
        }//总计耗时100*3+400=700ms
        println("collected cost:$measureTimeMillis4 ms")
    }
}

val flow1 = flow {
    kotlinx.coroutines.withContext(Dispatchers.Default) {//不能直接该上下文,或从不同上下文发射
        for (i in 1..3) {
            Thread.sleep(100)
            emit(i)
        }
    }
}
val flow2 = flow {
    for (i in 1..3) {
        Thread.sleep(100)
        log("emit:$i")
        emit(i)
    }
}.flowOn(Dispatchers.Default)//更改上下文的正确方式:flowOn操作符

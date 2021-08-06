package com.`as`.app2.coroutines

import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun simple5(): Flow<Int> = flow {
    for (i in 1..3) {
        println("Emitting $i")
        emit(i) // 发射下一个值
    }
}

fun simple51(): Flow<String> = flow {
    for (i in 1..3) {
        println("Emitting $i")
        emit(i) // 发射下一个值
    }
}.map { value ->
    check(value <= 1) { "crashed on $value" }
    "string $value"
}

// 异常操作符|完成操作符|取消操作符
// 当运算符中的发射器或代码抛出异常时，收集可以带有异常的完成
fun main(args: Array<String>) {
    runBlocking {
//        try {
//            simple5().collect {//1.在发射器/过渡或末端操作符中捕获异常,之后不再发出任何值
//                value -> println(value)
//                check(value<=1){"collected $value"}
//            }

//            simple51().collect {
//                    value ->
//                println(value)
//            }
//
//        } catch (e: Exception) {
//            println("catch $e")
//        }
        /**
         * 不能在 flow { ... } 构建器内部的 try/catch中发射异常, 这违反异常的透明性
         * 2.使用catch 操作符来保留此异常的透明性并允许封装它的异常处理
         * 可以使用 throw 重新抛出异常。
         * 可以使用 catch 代码块中的 emit 将异常转换为值发射出去。
         * 可以将异常忽略，或用日志打印，或使用一些其他代码处理它。
         */
//        simple51().catch { e -> emit("catch $e") }
//            .collect { value -> println(value) }

        //3.catch操作符只捕获它上游的异常, 并不处理下游的异常
//        simple5().catch { e -> println("catch $e") }//并不处理下游异常
//            .collect { value ->
//                check(value <= 1) { "colleted $value" }
//                println(value)
//            }

//        simple5().onEach { value ->
//            check(value <= 1) { "collected $value" }
//            println(value)
//        }.catch { e -> println("catch $e") }//4.将可能异常的代码放到catch之前声明
//            .collect { value -> println(value) }

        //a.收集器可以使用 try-finally 块在 collect 完成时执行一个动作
//        try {
//            simple5().collect { value -> println(value) }
//        } finally {
//            println("Done")
//        }
        //b.也可以用onCompletion 过渡操作符,处理流收集完成时的动作
        //onCompletion的表达式中有可空参数 Throwable,没异常时为空,有异常时为异常. 只用作判断是否有异常,并不处理异常

//        simple5().onCompletion { println("onCompletion") }.collect { value -> println(value) }
//        simple5().onCompletion { cause: Throwable? -> if (cause != null) println("onCompletion") }
//            .catch { cause: Throwable -> println("exception happened") }//即使onCompletion判断有异常也不处理,仍能catch到
//            .collect { value -> println(value) }

        //I.模仿addEventListener再removeEventListener的场景
//        events().onEach { event -> println("Event:$event") }
//            .collect()
//            println("other work")//collect后的代码一直等待流被收集完

        //II. 实际工作中launchIn(scope)的作用域是一个寿命有限的实体。
        //在该实体的寿命终止后，相应的作用域就会被取消，即取消相应流的收集。
        //即onEach { ... }.launchIn(scope) 工作方式不需要要类似的removeEventListener
//        events().onEach { event -> println("Event:$event") }
//            .launchIn(this)//接收一个CoroutineScope,表示用哪个协程来启动流的收集
//            println("other work")//collect后的代码一直等待流被收集完
        //III.构建的流可以取消,取消后抛出 CancellationException；
//        events().collect {
//                value -> if (value == 2) cancel()
//            println(value) }

    }
}

fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }

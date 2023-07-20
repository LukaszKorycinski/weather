package com.empik.weather.ui.screens.city_search

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.junit.Test

class TestCorutine  {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Test
    fun testSimple() {
        runBlocking {
            val supervisorJob = SupervisorJob()
            val newJob = launch(supervisorJob) {
                println("jobs#1: ${currentCoroutineContext().job == supervisorJob}" )
                println("jobs#2: ${supervisorJob.children.contains(currentCoroutineContext().job)}" )
                launch {
                    delay(500)
                    println("coroutine 1")
                }
                launch {
                    throw IllegalStateException("intended")
                    println("coroutine 2")
                }
            }
            println("jobs#3: ${newJob == supervisorJob}" )
            newJob.join()
        }
    }


    @Test
    fun testSimple2() {
        runBlocking {
            supervisorScope {
                launch {
                    delay(500)
                    println("coroutine 1")
                }
                launch {
                    throw IllegalStateException("intended")
                    println("coroutine 2")
                }
            }
            Thread.sleep(1000)
        }
    }


    @Test
    fun testOk() {
        scope.launch {
            delay(500)
            println("coroutine 1")
        }
        scope.launch {
            throw IllegalStateException("intended")
            println("coroutine 2")
        }
        Thread.sleep(1000)
    }


//    @Test
//    fun testSecond() {
//        scope.launch(SupervisorJob()) {
//            println("launch: " + coroutineContext)
//            println("launch: " + coroutineContext.job)
//            println("launch: " + currentCoroutineContext())
//            println("launch: " + currentCoroutineContext().job)
//            launch {
//                delay(500)
//                println("coroutine 1")
//            }
//            launch {
//                throw IllegalStateException("intended")
//                println("coroutine 2")
//            }
//        }
//        println("scope: " + scope)
//        println("scope: " + scope.coroutineContext)
//        println("scope: " + scope.coroutineContext.job)
//        Thread.sleep(1000)
//    }

}
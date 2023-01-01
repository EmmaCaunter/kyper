#!/usr/bin/env kotlinc -script

@file:DependsOn("/Users/pgreze/git/pgreze/kyper/build/libs/kyper.jar")

import com.github.pgreze.kyper.Help
import com.github.pgreze.kyper.kyper

@Help("Say hello in English")
fun hello(name: String) {
    println("hello $name")
}

@Help("Say hello in French")
fun bonjour(name: String) {
    println("bonjour $name")
}

kyper(help = "Say hello to your user").invoke(args)

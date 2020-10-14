package pl.pollub.bsi

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("pl.pollub.bsi")
		.start()
}


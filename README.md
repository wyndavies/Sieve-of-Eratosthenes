# Sieve-of-Eratosthenes
A java implementation of the Sieve of Eratosthenes, using multi-threading

I wanted to experiment with multi-threading and inter-thread communication in Java, so I implemented the Sieve of Eratosthenes. This is a process for finding Prime Numbers that lends itself well to multi-threading.

This is an implementation of the sieve of Eratosthenes. He worked out the first reliable way of finding
prime numbers. It also lends itself well to multi-threading.
We pick a maximum number and then use sifters to find all the prime numbers from 2 to max.
First we take the smallest number in the list - 2 - and use this as the first sifter.
We remove all numbers that can be evenly divided by 2 from the list. The smallest number left is a prime
In this case it is 3. We then use this as the next sifter and repeat. We can stop creating sifters once
we have reached the square root of max value. All numbers left and all sifters are prime numbers.
 
To replicate this as a multi-threaded program is pretty much the same logic. We create a sifter with a
key of 2 to filter out all multiples of itself. We also have a feeder, feeding in all the numbers from
3 to max in order. It then sends a value of -1, which means 'finished'
If the sifter can't divide the number it has received evenly it tries to pass this number on to the next
sifter.
If there isn't a next sifter it has found a prime and it records this in the results window.
If the new value is less than the square root of max value it also creates another sifter using the
new prime as its key and will send all subsequent numbers to this sifter.
When the sifter receives a value of -1 it knows the list is finished and it shuts down.

This isn't a very efficient way of finding prime numbers, but it is a good way to show multi-threading
and inter-thread communications.

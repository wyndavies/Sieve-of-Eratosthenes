# Sieve-of-Eratosthenes
A java implementation of the Sieve of Eratosthenes, using multi-threading

I wanted to experiment with multi-threading and inter-thread communication in Java, so I implemented the Sieve of Eratosthenes. This is a process for finding Prime Numbers that lends itself well to multi-threading.

A maximum value is selected and each value from 2 to max-value is tested to see if it is prime.
The process is simple:
Starting with 2 as a sifter, all values are checked to see if they can be divided by this number.
If they can, they are removed and the lowest remaining value is identifed as prime.
This new 'sifter' is now used to checked all remaining values are checked to see if they can be divided.
This operation continues using the newest 'sifter' is more than the square root of the max-value. All remaining values are consider to be prime numbers.

The program starts with 3 threads:
- A feeder thread that feeds in values from 3 to max-value
- A sifter with the key of 2
- A results thread

The feeder sends the values to the sifter in numerical order.
The sifter tests each value it receives to see if it can divide it evenually by its key.
If it can it throws the value away, otherwise it must make a decision
- It tries to pass it on to the next thread
- If the next thread is null it then makes a choice
  - If the value is greater than the square-root of max-value, it links to the results thread and sends the values there
  - Otherwise it creates a new sifter with the key of the value it has and links to that thread
  
The feeder sends a value of -1 as the last number, which is the signal for the threads to shut down.

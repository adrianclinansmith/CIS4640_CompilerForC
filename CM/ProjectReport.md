# Checkpoint 3 Project Report
##### By: Adrian Clinansmith and Xiangyuan Lian

> Note: this document uses markdown with embedded C and Java code

## Table of Contents

1. [Development Process](#Development-Process)
2. [Improvements](#Improvements)
3. [Reflections](#Reflections)

## Development Process

Our team broadly followed the development plan as outlined in **11-TMSimulator slide 25** to develope the code generator. Our steps were as follows:

1. Get familiar with the TM Simulator and its assembly instructions (_both_)
2. Implement the prelude, input and output functions, and finale (_Xiangyuan_)
3. Implement enough functionality to compile an empty main function (_Xiangyuan_)
4. Implement enough functionality to compile fac.cm (_Adrian_)
5. Implement enough functionality to compile gcd.cm (_Adrian_)
6. Implement enough functionality to compile sort.cm (_Adrian_)
7. Implement out-of-bounds errors (_Xiangyuan_)
8. Check for certain edge cases (_both_)

##### Learning TM instructions

Learning TM assembly instructions was straightforward. Implementing the prelude and finale was also not difficult, because they were provided in the slides.  However, once the codebase started growing, the long lines of instructions could be difficult to decipher, even with comments on every line.

##### Compiling fac.cm

Compiling fac.cm was somewhat challenging, because it contains a comparison operator (<, >, <=, >=, ==) for the while-test. We tried to implement the operators so they could be used in any expression, returning 1 for true and 0 for false, just like C. However, the jump instructions (JLT, JLE, JGT, JGE, JEQ, JNE) must use the pc register, so special locations in iMem would have had to be set aside to hold 0 and 1. This seemed like a messy solution, so instead we assumed the comparison operators would only appear within test conditions, and then simply jump over the body if the test failed.

The call to input() in fac.cm is where we first setup the frame calling-sequence as outlined in **11-TMSimulator slide 12**. This was relatively straight forward because the steps were outlined in detail.

##### Compiling gcd.cm

gcd.cm has the first if-else statement. Jumping over the if-body was implemented in  much the same way as jumping over function definitions. At the bottom of the if-body another jump condition was added so the else-body could be skipped. 

##### Compiling sort.cm

Sort was by far the most difficult program to implement. To pass an array to a function, we pass-by-reference by storing the memory location of the array in the frame, instead of copying the array as with int variables. This works fine for simple function calls, however, sort.cm passes the array's reference to another function. Specifically, the reference _a[]_ from _sort()_ gets passed to _minloc()_. 

```c
void sort(int a[], int low, int high) { 
    ...
    k = minloc(a, i, high);
    ...
}
```

When we ran this, the memory address of the parameter itself was being passed, not the original array's address. This resulted in unusual behavior because the frame was being overwritten with values meant for the array. Consequently, it took a fair amount of time to discover the issue. Once found, our solution was to mark the argument in the Abstract Syntax Tree as either an array, or a reference to an array. If it's the former, it gets passed-by-reference, and if it's the latter then it gets passed-by-value, so the same address will be stored in each subsequent frame.

```java
public class ArrayDec extends VarDec {
    ...
    public boolean isArrayPtr() {...}
    ...
}
```

sort.cm was also the first program to use a global variable. The given _prelude_ instructions added the maximum memory address to _gp_, which was helpful. Therefore all that needed to be done was to move the base of _fp_ up as global variables get stored. 

##### Out of bounds error checking

Two HALT instruction are emitted each time an indexed variable is encountered. Before each HALT there is a jump instruction which checks if the index is less than the array's size, and then if it is greater than zero. 
```
HALT: 0,0,0  * normal halt
HALT: 1,0,0  * index >= size
HALT: 2,0,0  * index < 0
```
Fei Song had suggested outputting large negative numbers before halting to show such errors. However, we decided it made more sense to add the error code directly to the halt statement itself, since it has no effect and it always gets displayed when the program terminates.

##### Handling edge cases

Nested compound-statements required no additional coding. Such statements may store additional declarations in the current stack-frame underneath the previous declarations, without the need to create another frame. Luckily this was already implemented in the while and if statements.

Our two previous CM programs simply outputted the results to stdin or stderr because there was nothing to execute. Now it outputs to a given file, or stdin if no file is provided.

## Improvements

This project was quite time consuming, so we weren't able to look too deeply into potential improvements from the previous checkpoints. However, both checkpoints 1 and 2 were already fully functional, so there likely wouldn't be much to change. The codebases for some absyn classes had been cleaned up a little without compromising the original functionality. Also, error outputs from the parser and the semantic analyzer were slightly altered so they would look more congruent.

## Reflections

This was by far the most challenging of the three checkpoints. What made it particularity difficult was the need to constantly track of the relationships between:
- the Java syntax tree, 
- the emitted iMem instructions, and 
- the resulting dMem frames that would change during execution. 

Consequently, it seemed as though the Java we wrote was always a few steps removed from what would actually happen when the tm instructions were run. As such, it could be difficult keeping a mental map of everything that was happening. 

Also, somehow we didn't realize there was already compiled code for the three test programs. So we didn't look at that code at all until near the end of the development process. Therefore our code will likely be different from what's expected, although it works just as it should.

Overall, creating the CM compiler was a challenging yet rewarding experience, and an excellent last assignment for our final year.
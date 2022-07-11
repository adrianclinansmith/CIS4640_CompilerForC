* prelude:
  0:    LD 6, 0(0)      load MAX_ADDRESS to gp
  1:   LDA 5, 0(6)      load MAX_ADDRESS to fp
  2:    ST 0, 0(0)      clear address 0
* input routine:
  3:   LDA 7, 7(7)      jump over i/o code
  4:    ST 0, -1(5)     store return
  5:    IN 0, 0, 0      input
  6:    LD 7, -1(5)     return to caller
* output routine:
  7:    ST 0, -1(5)     store return
  8:    LD 0, -2(5)     load output value
  9:   OUT 0, 0, 0      output
 10:    LD 7, -1(5)     return to caller
* main routine:
 12:    ST 0, -1(5)     store return
 13:   LDA 0, -2(5)     load var 'x' addr to ac
 14:    ST 0, -3(5)     store 'x' addr in memory
 15:   LDC 0, 1(0)      load integer 1 to ac
 16:    ST 0, -4(5)     store integer 1 in memory
 17:    LD 0, -3(5)     load the address to assign into ac0
 18:    LD 1, -4(5)     load the assignment's RHS into ac1
 19:    ST 1, 0(0)      store assign's RHS to the var location
 20:    ST 1, -3(5)     store assign's RHS to memory
 21:   LDA 0, -3(5)     load var 'x' addr to ac
 22:    ST 0, -4(5)     store 'x' addr in memory
 23:   LDC 0, 2(0)      load integer 2 to ac
 24:    ST 0, -5(5)     store integer 2 in memory
 25:    LD 0, -4(5)     load the address to assign into ac0
 26:    LD 1, -5(5)     load the assignment's RHS into ac1
 27:    ST 1, 0(0)      store assign's RHS to the var location
 28:    ST 1, -4(5)     store assign's RHS to memory
* - call to output
 29:    LD 0, -3(5)     load var 'x' to ac
 30:    ST 0, -6(5)     store 'x' in memory
 31:    ST 0, -6(5)     store argument in frame
 32:    ST 5, -4(5)     store caller's fp
 33:   LDA 5, -4(5)     push new frame
 34:   LDA 0, 1(7)      save return loc in ac
 35:   LDC 7, 7(0)      jump to function entry
 36:    LD 5, 0(5)      pop current frame
* - finished call to output
* - call to output
 37:    LD 0, -2(5)     load var 'x' to ac
 38:    ST 0, -5(5)     store 'x' in memory
 39:    ST 0, -5(5)     store argument in frame
 40:    ST 5, -3(5)     store caller's fp
 41:   LDA 5, -3(5)     push new frame
 42:   LDA 0, 1(7)      save return loc in ac
 43:   LDC 7, 7(0)      jump to function entry
 44:    LD 5, 0(5)      pop current frame
* - finished call to output
 45:    LD 7, -1(5)     return to caller
 11:   LDA 7, 34(7)     jump over main
* finale:
 46:    ST 5, 0(5)      push ofp
 47:   LDA 5, 0(5)      push frame
 48:   LDA 0, 1(7)      load ac with ret ptr
 49:   LDC 7, 12(0)     jump to main loc
 50:    LD 5, 0(5)      pop frame
 51:  HALT 0, 0, 0
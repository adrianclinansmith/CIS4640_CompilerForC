* Standard prelude:
  0:     LD  6,0(0) 	load gp with MAX_ADDRESS    gp := mem(ac0)
  1:    LDA  5,0(6) 	copy gp to fp               fp := gp                     
  2:     ST  0,0(0) 	clear location 0            mem(0) = ac0
* Jump around i/o routines here
* code for input routine
  4:     ST  0,-1(5) 	store return                mem(fp - 1) := ac0                
  5:     IN  0,0,0 	    input                       ac0 := input
  6:     LD  7,-1(5) 	return to caller            pc := mem(fp - 1)
* code for output routine
  7:     ST  0,-1(5) 	store return                mem(fp - 1) := ac0
  8:     LD  0,-2(5) 	load output value           ac0 := mem(fp - 2)
  9:    OUT  0,0,0 	output                          output := ac0
 10:     LD  7,-1(5) 	return to caller            pc := mem(fp - 1)
  3:    LDA  7,7(7) 	jump around i/o code        pc := pc + 7
* End of standard prelude.
* processing function: main
* jump around function body here
 12:     ST  0,-1(5) 	store return                mem(fp - 1) := ac0
* -> compound statement
* processing local var: x
* processing local var: fac
* -> op
* -> id
* looking up id: x
 13:    LDA  0,-2(5) 	load id address             ac0 := fp - 2
* <- id
 14:     ST  0,-4(5) 	op: push left               mem(fp - 4) = ac0
 11:    LDA  7,52(7) 	jump around fn body         pc := pc + 52
* <- fundecl
 64:     ST  5,0(5) 	push ofp                    mem(fp) := fp
 65:    LDA  5,0(5) 	push frame                  fp := fp
 66:    LDA  0,1(7) 	load ac with ret ptr        ac0 := pc + 1
 67:    LDA  7,-56(7) 	jump to main loc            pc := pc - 56
 68:     LD  5,0(5) 	pop frame                   fp := mem(fp)
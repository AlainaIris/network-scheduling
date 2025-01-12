# Network Scheudling

Network Scheduling is a complex problem which involves taking into account
time, importance of relationships, and comprehendable solutions. This
program gives an approximation of the ideal schedule for a network of
individuals. This work is based on the paper "Polyamorous Scheduling"
by Leszek Gąsieniec, Benjamin Smith, and Sebastian Wild. That paper can be
found here: https://arxiv.org/pdf/2403.00465

Edge coloring for layers is done by Misra & Gries edge-coloring algorithm.
Any errors, improvements, or other feedback can be directed to my email
at alainairis@proton.me

## Notes:
- Each individual may only meet one other individual per day
- Approximation ratio is less than or equal to 3lg(|V|)
- Misra & Gries edge-coloring may use up to one extra color
- Runtime is O(log(|E|)) for scheduling, O(|V||E|) for M&G edge coloring

## Commands
This program comes with 3 seperate modes.

Mode 1: A single instance of a network is made. Each step is shown, with
        the resultant schedule being provided. Additional specs such as
	approximation ratio (of single run) are provided.
	
    	java Driver -e

Mode 2: A performance test which measures time of runs, providing input
    	information. Add -d flag to get output as a list of data points.
	
    	java Driver -p [-d]

Mode 3: A single instance test based on user input.

    	java Driver -i [input-file]

ALL input files must be formatted in CSV format as follows:

	A,B,C,D...<br>
	n,n,n,...<br>
	n,n,n,...<br>
	.<br>
	.<br>
	.<br>
	n,n,n,...

Wherein:
    	The first row is the names of the individuals in order.
	
    	All remaining rows form a SQUARE matrix in which n[i,j]
    	represents the relationship between names[i] and names[j].

    	IMPORTANT: This matrix is read as an upper triangular matrix to
    	minimize user error. PLEASE do not enter input using a lower
    	triangular matrix. Failure to provide accurate and formatted input
    	will result in unintended behaviors. Below is the default example
    	as a CSV for reference.

Alice,Belle,Claire,Daisy,Emily,Felix,Grace,Holly<br>
0,40,0,80,0,40,0,0<br>
0,0,80,0,0,0,0,0<br>
0,0,0,16,0,0,0,0<br>
0,0,0,0,20,0,16,0<br>
0,0,0,0,0,40,0,80<br>
0,0,0,0,0,0,40,0<br>
0,0,0,0,0,0,0,0<br>
0,0,0,0,0,0,0,0<br>

# Dinning-Philosophers
Implenting Dinning Philosophers problem in a client-server style with nice GUI using java and JavaFX

## Dinning Philosophers Problem
The problem of dining philosophers is a classic problem in computer science that illustrates the challenges of concurrency and synchronization. The problem can be described as follows:

- There are five philosophers sitting around a circular table. Each philosopher has a bowl of rice and a chopstick on each side. The chopstick on the right of each philosopher is the same as the chopstick on the left of the next philosopher.
- The philosophers alternate between thinking, eating, and waiting. To eat, a philosopher must pick up both chopsticks on his sides. The only communication between the philosophers is through picking up and dropping the chopsticks (assume they do not talk or write).
- The problem is to design an algorithm that allows the philosophers to eat without causing any deadlock, starvation, or unfairness.

Some possible scenarios that may occur are:

- If all the philosophers decide to eat at the same time, they will all succeed in the first step of the algorithm and pick up their right chopsticks. However, in the second step, they will all wait indefinitely for their left chopsticks. This situation is called a deadlock.
- If one of the philosophers who has picked up his right chopstick and is waiting for his left chopstick decides to drop his chopstick and sit quietly and watch the others eat, this may create a possibility that the altruistic philosopher never gets a chance to eat. This situation is called starvation.
- Even if all the philosophers manage to eat, it is possible that some of them get more opportunities to eat than others. This situation is called unfairness.

The task is to propose solutions for the problems mentioned in the algorithm and modify the algorithm accordingly. Are there any other problems that may arise?


#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <semaphore.h>

float serial_pi(int precision);

// Global variables
int number_of_threads, precision;
float global_sum = 0;

// Semaphore lock
sem_t lock;

float serial_pi(int precision);
void *thread_function(void *rank);

int main(int argc, char* argv[]){

    float local_sum = 0;

    // Invalid input
    if (argc != 3) {
        printf("Usage: <precision> <number_of_threads>\n");
        return 1;
    }

    // Retrieve values from the command line
    precision = atoi(argv[1]);
    number_of_threads = atoi(argv[2]);
    
    // Serial calculation of pi
    float serial_sum = serial_pi(precision);
    printf("Serially calculated sum: %f\n", serial_sum);

    /* Multithreaded calculation of pi */
    // Calculate the steps per thread
    int steps_per_thread = precision / number_of_threads;
    int remaining_steps = precision % number_of_threads;

    // Invalid calculation of steps per thread
    if (steps_per_thread <= 0) {
        printf("Invalid steps per thread: %d\n", steps_per_thread);
        return 2;
    }

    pthread_t *threads = (pthread_t*) malloc (number_of_threads * sizeof(pthread_t));

    // Initialize the semaphore lock
    // 0 is for sharing between threads
    // 1 is the initial value of it
    sem_init(&lock, 0, 1);

    // Create threads
    for (int i = 0; i < number_of_threads; i++)
        pthread_create(&threads[i], NULL, thread_function, (void *) i);

    // Wait for threads to finish
    for (int i = 0; i < number_of_threads; i++)
        pthread_join(threads[i], NULL);

    // The process checks for any remaining calculations
    if (remaining_steps == precision) {
        printf("Pi can only be calculated serially\n");
        return 4;
    }
    else {
        int start_index = precision - remaining_steps;
        float factor;

        if (start_index % 2 == 0)
            factor = 1;
        else
            factor = -1;

        for (int i = start_index; i < precision; i++, factor = -factor)
            local_sum += factor/(2*i+1);
    }

    global_sum *= 4;

    printf("Sum calculated with threads: %f\n", global_sum);

    // Destroy the semaphore
    sem_destroy(&lock);

    free(threads);

    return 0;
    

}

float serial_pi(int precision) {

    float sum = 0.0;
    float factor = 1.0;

    for (int i = 0; i < precision; i++, factor = -factor)
        sum += factor/(2*i+1);

    return 4 * sum;

}

void *thread_function(void *rank) {

    // Retrieve the thread's rank
    int *local_ptr = &rank;
    int local_rank = *local_ptr;

    // Calculate the starting and finishing index
    int start_index = (precision / number_of_threads) * local_rank;
    int finish_index = start_index + (precision / number_of_threads);

    // Every thread has its own local sum and factor
    float local_sum = 0;
    float factor;

    if (start_index % 2 == 0)
        factor = 1;
    else
        factor = -1;

    for (int i = start_index; i < finish_index; i++, factor = -factor)
        local_sum += factor / (2 * i + 1);

    // Attempting to acquire the semaphore
    sem_wait(&lock);

    global_sum += local_sum;

    // Releasing the semaphore
    sem_post(&lock);

    pthread_exit(NULL);

}

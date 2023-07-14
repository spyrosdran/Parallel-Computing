#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

float serial_pi(int precision);

// Global variables
int number_of_threads, precision;

// thread_sum stores the individual threads' local sums
float *thread_sum;

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
    thread_sum = (float*) malloc (number_of_threads * sizeof(float));

    // Initialize thread sum
    for (int i = 0; i < number_of_threads; i++)
        thread_sum[i] = 0;

    // Create threads
    for (int i = 0; i < number_of_threads; i++)
        pthread_create(&threads[i], NULL, thread_function, (void *) i);

    // Wait for threads to finish
    for (int i = 0; i < number_of_threads; i++)
        pthread_join(threads[i], NULL);

    // The process checks for any remaining calculations
    if (remaining_steps == precision) {
        printf("Pi can only be calculated serially\n");
        return 3;
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

    // Calculate the total sum
    float total_sum = 0;

    for (int i = 0; i < number_of_threads; i++)
        total_sum += thread_sum[i];

    total_sum += local_sum;
    total_sum *= 4;

    printf("Sum calculated with threads: %f\n", total_sum);

    free(threads);
    free(thread_sum);

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
        thread_sum[local_rank] += factor / (2 * i + 1);

    pthread_exit(NULL);

}
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

// Global variables
int *x, *y, n;
int steps_per_thread, remaining;
pthread_mutex_t lock;

// Function signature
void *thread_function(void *rank);

int main(int argc, char *argv[])
{
    pthread_t *threads;

    if (argc != 3)
    {
        printf("Usage: %s <array_size> <number_of_threads>\n", argv[0]);
        return 1;
    }

    n = atoi(argv[1]);
    int number_of_threads = atoi(argv[2]);

    // Allocate memory for the arrays
    x = (int *)malloc(n * sizeof(int));
    y = (int *)malloc(n * sizeof(int));

    // Checking if the steps per thread are valid
    steps_per_thread = n / number_of_threads;
    remaining = n % number_of_threads;

    if (steps_per_thread <= 0)
    {
        printf("Invalid steps per thread: %d\n", steps_per_thread);
        return 2;
    }

    // Initialize the x array
    for (int i = 0; i < n; i++)
        x[i] = n - i;

    // Initialize mutex lock
    if (pthread_mutex_init(&lock, NULL) != 0)
    {
        printf("Mutex init has failed\n");
        return 5;
    }

    // Create threads
    threads = (pthread_t *)malloc(number_of_threads * sizeof(pthread_t));
    for (int i = 0; i < number_of_threads; i++)
        pthread_create(&threads[i], NULL, thread_function, (void *)i);

    // Wait for threads to finish
    for (int i = 0; i < number_of_threads; i++)
        pthread_join(threads[i], NULL);

    // Process checks if there is a remaining part of the table to be sorted
    if (remaining > 0)
    {

        int my_num, my_place, i, j;
        long start_index = n - remaining;
        long finish_index = n;

        // Count sort
        for (j = start_index; j < finish_index; j++)
        {
            my_num = x[j];
            my_place = 0;
            for (i = 0; i < n; i++)
                if ((my_num > x[i]) || ((my_num == x[i]) && (j < i)))
                    my_place++;
            y[my_place] = my_num;
        }
    }

    // Print sorted array
    // for (int i=0; i<n-1; i++)
    //     printf("%d\n", y[i]);

    // Validation check
    for (int i = 0; i < n - 1; i++)
        if (y[i] > y[i + 1])
        {
            printf("*** Array not sorted ***\n");
            printf("y[%d] > y[%d]\n", y[i], y[i + 1]);
            printf("%d > %d\n", y[i], y[i + 1]);
            printf("Terminating\n");
            return -1;
        }

    printf("Validation check passed\n");

    // Destroy mutex lock
    pthread_mutex_destroy(&lock);

    free(x);
    free(y);

    return 0;
}

void *thread_function(void *rank)
{

    // Create local variables
    int *local_ptr = &rank;
    int local_rank = *local_ptr;

    int position[steps_per_thread];
    int number[steps_per_thread];
    int counter = 0;

    int start_index = local_rank * steps_per_thread;
    int finish_index = start_index + steps_per_thread;
    int i, j, my_num, my_place;

    // Count sort
    for (j = start_index; j < finish_index; j++)
    {
        my_num = x[j];
        my_place = 0;
        for (i = 0; i < n; i++)
            if ((my_num > x[i]) || ((my_num == x[i]) && (j < i)))
                my_place++;
        position[counter] = my_place;
        number[counter] = my_num;
        counter++;
    }

    // Update global y
    pthread_mutex_lock(&lock);

    for (i = 0; i < counter; i++)
        y[position[i]] = number[i];

    pthread_mutex_unlock(&lock);

    pthread_exit(NULL);
}

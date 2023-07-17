#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>

// Global variables
int number_of_threads, pattern_size, *match;
long file_size, steps_per_thread, match_size, total_matches;
char *buffer, *pattern;
pthread_mutex_t match_lock, total_matches_lock;

// Function signature
void *thread_function(void *rank);

int main(int argc, char *argv[])
{

    FILE *pFile;
    char *filename;
    size_t result;
    long i, j;
    pthread_t *threads;

    if (argc != 4)
    {
        printf("Usage: %s <file_name> <pattern> <number_of_threads>\n", argv[0]);
        return 1;
    }

    filename = argv[1];
    pattern = argv[2];
    number_of_threads = atoi(argv[3]);

    if (number_of_threads <= 1)
    {
        printf("There should be at least 2 threads\n");
        printf("Terminating...\n");
        return -1;
    }

    // Open the file
    pFile = fopen(filename, "rb");
    if (pFile == NULL)
    {
        printf("File error\n");
        return 2;
    }

    // Obtain file size
    fseek(pFile, 0, SEEK_END);
    file_size = ftell(pFile);
    rewind(pFile);
    printf("file size is %ld\n", file_size);

    // Checking if the steps per thread are valid
    steps_per_thread = file_size / number_of_threads;
    long remaining = file_size % number_of_threads;

    if (steps_per_thread <= 0)
    {
        printf("Invalid steps per thread: %ld\n", steps_per_thread);
        return 2;
    }

    // Allocate memory to contain the file:
    buffer = (char *)malloc(sizeof(char) * file_size);
    if (buffer == NULL)
    {
        printf("Memory error\n");
        return 3;
    }

    // Copy the file into the buffer:
    result = fread(buffer, 1, file_size, pFile);
    if (result != file_size)
    {
        printf("Reading error\n");
        return 4;
    }

    // Initializations
    pattern_size = strlen(pattern);
    match_size = file_size - pattern_size + 1;

    match = (int *)malloc(sizeof(int) * match_size);
    if (match == NULL)
    {
        printf("Malloc error\n");
        return 5;
    }

    total_matches = 0;
    for (j = 0; j < match_size; j++)
    {
        match[j] = 0;
    }

    // Initialize mutex locks
    if (pthread_mutex_init(&match_lock, NULL) != 0)
    {
        printf("Mutex init has failed\n");
        return 6;
    }

    if (pthread_mutex_init(&total_matches_lock, NULL) != 0)
    {
        printf("Mutex init has failed\n");
        return 7;
    }

    // Create threads
    threads = (pthread_t *)malloc(number_of_threads * sizeof(pthread_t));
    for (int i = 0; i < number_of_threads; i++)
        pthread_create(&threads[i], NULL, thread_function, (void *)i);

    // Wait for threads to finish
    for (int i = 0; i < number_of_threads; i++)
        pthread_join(threads[i], NULL);

    // Process checks if there is a remaining part
    if (remaining >= pattern_size)
    {

        long start_index = file_size - remaining;

        for (j = start_index; j < file_size; ++j)
        {

            for (i = 0; i < pattern_size && pattern[i] == buffer[i + j]; ++i)
                ;

            if (i >= pattern_size)
            {
                match[j] = 1;
                total_matches++;
            }
        }
    }

    // Process checks the parts between threads' slices
    for (i = 1; i < number_of_threads; i++)
    {

        long start = steps_per_thread * i - 1;
        long end = start + pattern_size;
        long k;

        for (j = start; j < end; ++j)
        {
            for (k = 0; k < pattern_size && pattern[k] == buffer[k + j]; ++k)
                ;

            if (k >= pattern_size && match[j] != 1)
            {
                match[j] = 1;
                total_matches++;
            }
        }
    }

    // Destroy mutex locks
    pthread_mutex_destroy(&match_lock);
    pthread_mutex_destroy(&total_matches_lock);

    /*
   for (j = 0; j < match_size; j++){
       printf("%d", match[j]);
   }
   */

    printf("Total matches = %ld\n", total_matches);

    fclose(pFile);
    free(buffer);
    free(match);

    return 0;
}

void *thread_function(void *rank)
{

    // Create local variables
    int *local_ptr = &rank;
    int local_rank = *local_ptr;

    int local_match[steps_per_thread];
    long local_total_matches = 0;
    long i, j;

    // Initialize local match
    for (i = 0; i < steps_per_thread; i++)
        local_match[i] = 0;

    // Define buffer slice
    long start_index = local_rank * steps_per_thread;
    long finish_index = start_index + steps_per_thread;

    // Brute force string matching
    for (j = start_index; j < finish_index; ++j)
    {

        for (i = 0; i < pattern_size && pattern[i] == buffer[i + j]; ++i)
            ;

        if (i >= pattern_size)
        {
            local_match[j % steps_per_thread] = 1;
            local_total_matches++;
        }
    }

    // Update global total matches
    pthread_mutex_lock(&total_matches_lock);

    total_matches += local_total_matches;

    pthread_mutex_unlock(&total_matches_lock);

    // Update global matches table
    pthread_mutex_lock(&match_lock);

    for (i = 0; i < steps_per_thread; i++)
        match[local_rank * steps_per_thread + i] = local_match[i];

    pthread_mutex_unlock(&match_lock);

    pthread_exit(NULL);
}
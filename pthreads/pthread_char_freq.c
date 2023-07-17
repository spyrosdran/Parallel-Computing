#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#define N 128
#define base 0

// Global frequency table
int freq[N];

// Global variables
int number_of_threads;
long file_size, steps_per_thread;
char *buffer;
pthread_mutex_t lock;

// Function signature
void *thread_function(void *rank);

int main(int argc, char *argv[])
{

    FILE *pFile;
    char *filename;
    size_t result;
    int i, j;
    pthread_t *threads;

    if (argc != 3)
    {
        printf("Usage: %s <file_name> <number_of_threads>\n", argv[0]);
        return 1;
    }

    number_of_threads = atoi(argv[2]);

    // Open the file
    filename = argv[1];
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

    // Initialize the frequency table
    for (j = 0; j < N; j++)
        freq[j] = 0;

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

    // Process checks if there is a remaining part
    if (remaining > 0)
    {

        long start_index = file_size - remaining;
        long finish_index = file_size;

        for (i = start_index; i <= finish_index; i++)
            freq[buffer[i] - base]++;
    }

    for (j = 0; j < N; j++)
        printf("%d = %d\n", j + base, freq[j]);

    // Destroy mutex lock
    pthread_mutex_destroy(&lock);

    fclose(pFile);
    free(buffer);

    return 0;
}

void *thread_function(void *rank)
{

    // Create local variables
    int *local_ptr = &rank;
    int local_rank = *local_ptr;

    // Create local frequency table
    int local_freq[N];

    long start_index = local_rank * steps_per_thread;
    long finish_index = start_index + steps_per_thread;
    long i;

    // Find local frequency table
    for (i = start_index; i < finish_index; i++)
        local_freq[buffer[i] - base]++;

    // Update global frequency table
    pthread_mutex_lock(&lock);

    for (i = 0; i < N; i++)
        freq[i] += local_freq[i];

    pthread_mutex_unlock(&lock);

    pthread_exit(NULL);
}
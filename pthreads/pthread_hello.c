#include <stdio.h>
#include <pthread.h>

// This is the function to be executed as a thread
void *thread(void *ptr)
{
    int id = (int) ptr;
    printf("Hello from Thread #%d\n", id);
    return  ptr;
}

int main(int argc, char **argv)
{
    // Declare thread variables
    pthread_t thread1, thread2;

    int first = 1;
    int second = 2;

    // Start threads
    pthread_create(&thread1, NULL, *thread, (void *) first);
    pthread_create(&thread2, NULL, *thread, (void *) second);

    // Wait for threads to finish
    pthread_join(thread1,NULL);
    pthread_join(thread2,NULL);

    return 0;
}
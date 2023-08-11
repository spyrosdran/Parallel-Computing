from multiprocessing import Process, Queue


def process(start_index, finish_index, queue):
    local_sum = 0

    if start_index % 2 == 0:
        factor = 1
    else:
        factor = -1

    for i in range(start_index, finish_index):
        local_sum += factor / (2 * i + 1)
        factor = -factor

    queue.put(local_sum)


if __name__ == "__main__":

    # Ask user input
    processes = []
    number_of_processes = int(input("Number of processes: "))
    precision = int(input("Precision: "))

    # Calculate steps per process and any remaining steps
    steps_per_process = precision // number_of_processes
    remaining = precision % number_of_processes

    # This queue is shared among processes and it's going to store the results
    results = Queue()

    # Create processes
    for i in range(number_of_processes):
        processes.append(Process(target=process(i * steps_per_process, (i + 1) * steps_per_process, results)))

    # Start processes
    for i in range(number_of_processes):
        processes[i].start()

    # Join processes
    for i in range(number_of_processes):
        processes[i].join()

    # Check for any remaining steps
    if remaining > 0:

        start_index = precision - remaining
        finish_index = precision

        remaining_steps_process = Process(target=process(start_index, finish_index, results))

        remaining_steps_process.start()
        remaining_steps_process.join()

    # Reduction of the results
    final_sum = 0
    for i in range(number_of_processes):
        final_sum += results.get()

    if remaining > 0:
        final_sum += results.get()

    final_sum *= 4

    # Print the result
    print(f"Process estimated pi: {final_sum}")


from multiprocessing import Process, Value, Lock
from ctypes import c_float


def process(start_index, finish_index, global_sum, lock):
    local_sum = 0

    if start_index % 2 == 0:
        factor = 1
    else:
        factor = -1

    for i in range(start_index, finish_index):
        local_sum += factor / (2 * i + 1)
        factor = -factor

    # Update the global sum
    with lock:
        global_sum.value += local_sum


if __name__ == "__main__":

    # Ask user input
    processes = []
    number_of_processes = int(input("Number of processes: "))
    precision = int(input("Precision: "))

    # Calculate steps per process and any remaining steps
    steps_per_process = precision // number_of_processes
    remaining = precision % number_of_processes

    # This variable stores the global_sum as a shared variable among processes
    global_sum = Value(c_float, 0)

    # This lock is going to be used among the processes
    lock = Lock()

    # Create processes
    for i in range(number_of_processes):
        processes.append(Process(target=process(i * steps_per_process, (i + 1) * steps_per_process, global_sum, lock)))

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

        remaining_steps_process = Process(target=process(start_index, finish_index, global_sum, lock))

        remaining_steps_process.start()
        remaining_steps_process.join()

    global_sum.value *= 4

    # Print the result
    print(f"Process estimated pi: {global_sum.value}")


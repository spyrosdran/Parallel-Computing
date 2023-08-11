from multiprocessing.pool import Pool


def process(start_index, finish_index):
    local_sum = 0

    if start_index % 2 == 0:
        factor = 1
    else:
        factor = -1

    for i in range(start_index, finish_index):
        local_sum += factor / (2 * i + 1)
        factor = -factor

    return local_sum


if __name__ == "__main__":

    # Ask user input
    number_of_processes = int(input("Number of processes: "))
    precision = int(input("Precision: "))

    # Calculate steps per process and any remaining steps
    steps_per_process = precision // number_of_processes
    remaining = precision % number_of_processes

    # Create a process pool and the processes to be executed
    with Pool(number_of_processes) as pool:

        # Prepare the arguments
        arguments = [(i * steps_per_process, (i+1) * steps_per_process) for i in range(number_of_processes)]

        # Issue processes
        results = pool.starmap(process, arguments)

    total_sum = sum(results)

    # Check for any remaining steps
    if remaining > 0:

        start_index = precision - remaining
        finish_index = precision

        total_sum += process(start_index, finish_index)

    total_sum *= 4

    # Print the result
    print(f"Process estimated pi: {total_sum}")


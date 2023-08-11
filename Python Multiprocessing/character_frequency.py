# This code is completely inefficient. Do not use it in real world problems.
# This is just for demonstration purposes.

from multiprocessing import Pool, Manager
import numpy as np

N = 128

# This function is going to be executed from every process
def process(start_index, finish_index, shared_buffer, result):

    local_frequencies = [0] * N

    # Calculate frequencies
    for i in range(start_index, finish_index):
        char_code = ord(shared_buffer[i])
        local_frequencies[char_code] += 1

    result.extend(local_frequencies)

# Master process
if __name__ == "__main__":

    file_name = input("File name: ")

    # Open the specified file and read all the lines
    with open(file_name) as file:
        lines = file.read()

    # Create a manager
    manager = Manager()

    # Shared buffer among the processes, containing the lines from the fl
    shared_buffer = manager.list(lines)

    # This is also a shared list among processes
    result = manager.list()

    number_of_processes = int(input("Number of processes: "))
    steps_per_process = len(lines) // number_of_processes
    remaining = len(lines) % number_of_processes

    # Construct processes, put them into the process poll and execute them
    with Pool(number_of_processes) as pool:
        arguments = [(i * steps_per_process, (i + 1) * steps_per_process, shared_buffer, result) for i in range(number_of_processes)]
        pool.starmap(process, arguments)

    # Check for any remaining parts
    if remaining > 0:
        start_index = len(lines) - remaining
        finish_index = len(lines)
        process(start_index, finish_index, shared_buffer, result)

    # Reduction of the results
    final_frequencies = np.sum(result, axis=0)
    final_frequencies = final_frequencies.tolist()

    for i in range(N):
        print(f"{i} = {final_frequencies[i]}")

from multiprocessing import Process


# Function to be executed as a process
def func(rank):
    print(f"Hello from process {rank}!")


if __name__ == '__main__':
    # Create processes
    process1 = Process(target=func(1))
    process2 = Process(target=func(2))

    # Start processes
    process1.start()
    process2.start()

    # Join processes
    process1.join()
    process2.join()

    print("Finished!")

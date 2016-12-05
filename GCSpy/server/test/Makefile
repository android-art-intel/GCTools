all: dummy_server

dummy_server: dummy_server.c dummy_driver.c image_driver.c
	$(CC) -ggdb3 -I../include -I. $^ -o $@ -L../ -lgcspy -lpthread

.PHONY: run-server gdb-server

run-server:
	LD_LIBRARY_PATH=.. ./dummy_server 3000

gdb-server:
	LD_LIBRARY_PATH=.. gdb dummy_server

CC := gcc
LIB_EXT := so
GCSPY_SRC_FILES := \
	src/gcspy_array_input.c \
	src/gcspy_array_output.c \
	src/gcspy_buffered_input.c \
	src/gcspy_buffered_output.c \
	src/gcspy_color_db.c \
	src/gcspy_command_stream.c \
	src/gcspy_comm.c \
	src/gcspy_d_utils.c \
	src/gcspy_gc_driver.c \
	src/gcspy_gc_stream.c \
	src/gcspy_interpreter.c \
	src/gcspy_main_server.c \
	src/gcspy_shape_stream.c \
	src/gcspy_timer.c \
	src/gcspy_utils.c

GCSPY_OBJDIR := objs
GCSPY_LIBDIR := libs
GCSPY_OBJS := $(GCSPY_SRC_FILES:.c=.o)
GCSPY_CFLAGS := -D_LINUX_ -std=gnu11 -fPIC -ggdb3 -Wall -I./include
GCSPY_LDFLAGS := -shared -lpthread
GCSPY_LIB_NAME := libgcspy.$(LIB_EXT)

$(GCSPY_LIB_NAME) : $(GCSPY_OBJS)
	$(LD) -o $@ $(notdir $^) $(GCSPY_LDFLAGS)

%.o:%.c
	$(CC) $(GCSPY_CFLAGS) -c $<

clean:
	$(RM) *.o

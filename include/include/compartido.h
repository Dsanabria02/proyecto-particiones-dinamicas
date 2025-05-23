#ifndef COMPARTIDO_H
#define COMPARTIDO_H

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <time.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/sem.h>
#include <signal.h>
#include <errno.h>

#define CLAVE_MEMORIA 0x1234
#define CLAVE_SEMAFORO 0x5678
#define MAX_LINEAS 100

typedef struct {
    int estado;         // 0 = libre, 1 = ocupado
    pid_t pid_ocupante;
} LineaMemoria;

// Operaciones semafóricas
void wait_semaforo(int semid);
void signal_semaforo(int semid);

#endif



#ifndef ESTRUCTURA_MEMORIA_H
#define ESTRUCTURA_MEMORIA_H

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/sem.h>

#define CLAVE_MEMORIA 0x1234
#define CLAVE_SEMAFORO 0x5678
#define MAX_LINEAS 100

typedef struct {
    int estado;         // 0 = libre, 1 = ocupado
    pid_t pid_ocupante;
    int size;           // Tamaño del bloque asignado
} LineaMemoria;

// Operaciones semafóricas
void wait_semaforo(int semid);
void signal_semaforo(int semid);

#endif
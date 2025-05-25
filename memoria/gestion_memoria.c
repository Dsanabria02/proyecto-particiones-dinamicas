#include "../include/gestion_memoria.h"

// -------- ALGORITMOS DE ASIGNACION ---------------

void bestFit(LineaMemoria *mem, int semid, int pid, int size) {
    wait_semaforo(semid);
    int bestIndex = -1, bestSize = MAX_LINEAS + 1;

    for (int i = 0; i < MAX_LINEAS; i++) {
        if (mem[i].estado == 0 && mem[i].size >= size) {
            if (mem[i].size < bestSize) {
                bestSize = mem[i].size;
                bestIndex = i;
            }
        }
    }

    if (bestIndex != -1) {
        mem[bestIndex].estado = 1;
        mem[bestIndex].pid_ocupante = pid;
        printf("Proceso %d asignado en línea %d (Best-Fit)\n", pid, bestIndex);
    } else {
        printf("No hay espacio para el proceso %d\n", pid);
    }

    signal_semaforo(semid);
}


void firstFit(LineaMemoria *mem, int semid, int pid, int size) {
    wait_semaforo(semid);  // Bloqueamos acceso para evitar interferencias

    for (int i = 0; i < MAX_LINEAS; i++) {
        if (mem[i].estado == 0 && mem[i].size >= size) {  // Encuentra el primer bloque disponible
            mem[i].estado = 1;
            mem[i].pid_ocupante = pid;
            printf("Proceso %d asignado en línea %d (First-Fit)\n", pid, i);
            break;
        }
    }

    signal_semaforo(semid);  // Liberamos el acceso
}


void worstFit(LineaMemoria *mem, int semid, int pid, int size) {
    wait_semaforo(semid);  // Bloqueo para evitar interferencias

    int worstIndex = -1;
    int worstSize = -1;

    for (int i = 0; i < MAX_LINEAS; i++) {
        if (mem[i].estado == 0 && mem[i].size >= size) {
            if (mem[i].size > worstSize) {  // Encuentra el espacio más grande
                worstSize = mem[i].size;
                worstIndex = i;
            }
        }
    }

    if (worstIndex != -1) {
        mem[worstIndex].estado = 1;
        mem[worstIndex].pid_ocupante = pid;
        printf("Proceso %d asignado en línea %d (Worst-Fit)\n", pid, worstIndex);
    } else {
        printf("No hay espacio para el proceso %d\n", pid);
    }

    signal_semaforo(semid);
}
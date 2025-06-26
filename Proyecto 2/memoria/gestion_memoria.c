#include "../include/gestion_memoria.h"
#include "../include/bitacora.h"  // Asegurar que se incluya el registro

// -------- ALGORITMOS DE ASIGNACION ---------------

void firstFit(LineaMemoria *mem, int semid, int pid, int size) {
    wait_semaforo(semid);

    int i = 0;
    while (i < MAX_LINEAS) {
        if (mem[i].estado == 0) {
            int start = i;
            int count = 0;

            while (i < MAX_LINEAS && mem[i].estado == 0 && count < size) {
                count++;
                i++;
            }

            if (count >= size) {
                for (int j = start; j < start + size; j++) {
                    mem[j].estado = 1;
                    mem[j].pid_ocupante = pid;
                    mem[j].size = size;
                }
                printf("Proceso %d asignado de línea %d a %d (First-Fit)\n", pid, start, start + size - 1);
                registrar_evento("Asignado a memoria", pid, start, start + size - 1);  // Registro en bitácora
                signal_semaforo(semid);
                return;
            }
        } else {
            i++;
        }
    }

    printf("No hay espacio para el proceso %d\n", pid);
    signal_semaforo(semid);
}

void bestFit(LineaMemoria *mem, int semid, int pid, int size) {
    wait_semaforo(semid);

    int bestStart = -1;
    int bestSize = MAX_LINEAS + 1;

    int i = 0;
    while (i < MAX_LINEAS) {
        if (mem[i].estado == 0) {
            int start = i;
            int count = 0;

            while (i < MAX_LINEAS && mem[i].estado == 0) {
                count++;
                i++;
            }

            if (count >= size && count < bestSize) {
                bestStart = start;
                bestSize = count;
            }
        } else {
            i++;
        }
    }

    if (bestStart != -1) {
        for (int j = bestStart; j < bestStart + size; j++) {
            mem[j].estado = 1;
            mem[j].pid_ocupante = pid;
            mem[j].size = size;
        }
        printf("Proceso %d asignado de línea %d a %d (Best-Fit)\n", pid, bestStart, bestStart + size - 1);
        registrar_evento("Asignado a memoria", pid, bestStart, bestStart + size - 1);  // Registro en bitácora
    } else {
        printf("No hay espacio para el proceso %d\n", pid);
    }

    signal_semaforo(semid);
}

void worstFit(LineaMemoria *mem, int semid, int pid, int size) {
    wait_semaforo(semid);

    int worstStart = -1;
    int worstSize = -1;

    int i = 0;
    while (i < MAX_LINEAS) {
        if (mem[i].estado == 0) {
            int start = i;
            int count = 0;

            while (i < MAX_LINEAS && mem[i].estado == 0) {
                count++;
                i++;
            }

            if (count >= size && count > worstSize) {
                worstStart = start;
                worstSize = count;
            }
        } else {
            i++;
        }
    }

    if (worstStart != -1) {
        for (int j = worstStart; j < worstStart + size; j++) {
            mem[j].estado = 1;
            mem[j].pid_ocupante = pid;
            mem[j].size = size;
        }
        printf("Proceso %d asignado de línea %d a %d (Worst-Fit)\n", pid, worstStart, worstStart + size - 1);
        registrar_evento("Asignado a memoria", pid, worstStart, worstStart + size - 1);  // Registro en bitácora
    } else {
        printf("No hay espacio para el proceso %d\n", pid);
    }

    signal_semaforo(semid);
}

// -------- LIBERACIÓN DE MEMORIA ---------------
void liberar_memoria(LineaMemoria *mem, int semid, int pid) {
    wait_semaforo(semid);

    int inicio = -1, fin = -1;
    for (int i = 0; i < MAX_LINEAS; i++) {
        if (mem[i].pid_ocupante == pid) {
            if (inicio == -1) inicio = i;
            mem[i].estado = 0;
            mem[i].pid_ocupante = 0;
            mem[i].size = 0;
            fin = i;
        }

        printf("Proceso %d liberó memoria de línea %d a %d\n", pid, inicio, fin);
        registrar_evento("Memoria liberada", pid, inicio, fin);  // Registro en bitácora
    }


    signal_semaforo(semid);
}
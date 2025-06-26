#ifndef GESTION_MEMORIA_H
#define GESTION_MEMORIA_H

#include "estructura_memoria.h"

// Declaraciones de funciones (sin implementación)
void bestFit(LineaMemoria *mem, int semid, int pid, int size);
void firstFit(LineaMemoria *mem, int semid, int pid, int size);
void worstFit(LineaMemoria *mem, int semid, int pid, int size);

// ----- Información de procesos (para el espía) -----

#define MAX_PROCESOS 100
#define CLAVE_PROCESOS 0x1235
#define CLAVE_CONTADOR 0x1236

typedef enum {
    EJECUTANDO,
    BLOQUEADO,
    ACCEDIENDO_MEMORIA,
    FINALIZADO
} EstadoProceso;

typedef struct {
    int pid;
    EstadoProceso estado;
} ProcesoInfo;

#endif
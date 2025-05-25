#ifndef GESTION_MEMORIA_H
#define GESTION_MEMORIA_H

#include "estructura_memoria.h"

// Declaraciones de funciones (sin implementación)
void bestFit(LineaMemoria *mem, int semid, int pid, int size);
void firstFit(LineaMemoria *mem, int semid, int pid, int size);
void worstFit(LineaMemoria *mem, int semid, int pid, int size);

#endif
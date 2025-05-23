#ifndef BITACORA_H
#define BITACORA_H

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <time.h>

void registrar_evento(const char *accion, pid_t pid, int inicio, int fin);

#endif


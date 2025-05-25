#include "../include/estructura_memoria.h"
#include "../include/gestion_memoria.h"

int main() {
    int shmid = shmget(CLAVE_MEMORIA, MAX_LINEAS * sizeof(LineaMemoria), 0666);
    int semid = semget(CLAVE_SEMAFORO, 1, 0666);
    LineaMemoria *mem = (LineaMemoria *)shmat(shmid, NULL, 0);

    int pid = getpid();
    firstFit(mem, semid, pid, 15); // Simular asignación con First-Fit
    //worstFit(mem, semid, pid, 20); // Simular asignación con Worst-Fir
    //bestFit(mem, semid, pid, 10); // Simular asignación con Best-Fit
    

    mostrar_memoria(mem);
    shmdt(mem);

    return 0;
}
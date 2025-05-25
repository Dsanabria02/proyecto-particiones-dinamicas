#include "../include/estructura_memoria.h"
#include "../include/bitacora.h"

void inicializar_memoria(LineaMemoria *mem, int n) {
    for (int i = 0; i < n; i++) {
        mem[i].estado = 0;
        mem[i].pid_ocupante = 0;
        mem[i].size = 0;  // Asignar tamaño inicial
    }
}

int main() {
    int n;
    printf("Ingrese número de líneas de memoria (máx %d): ", MAX_LINEAS);
    scanf("%d", &n);

    if (n <= 0 || n > MAX_LINEAS) {
        fprintf(stderr, "Error: número inválido.\n");
        exit(1);
    }

    int shmid = shmget(CLAVE_MEMORIA, (sizeof(int) + sizeof(LineaMemoria) * n), IPC_CREAT | 0666);
    if (shmid == -1) {
        printf("----- INICIALIZADOR --------");
        perror("Error al crear memoria compartida");
        fprintf(stderr, "Valor de shmid: %d\n", shmid);
        fprintf(stderr, "Valor de n: %d, tamaño asignado: %ld\n", n, sizeof(LineaMemoria) * n);
        exit(1);
    }


    // LineaMemoria *mem = (LineaMemoria *)shmat(shmid, NULL, 0);
    int *mem_n = (int *)shmat(shmid, NULL, 0);
    mem_n[0] = n;  // Guarda `n` en el primer bloque de memoria
    LineaMemoria *mem = (LineaMemoria *)(mem_n + 1);  // Desplaza `mem` después de `n`
    if ((void *)mem == (void *)-1) {
        printf("----- INICIALIZADOR --------");
        perror("shmat");
        exit(1);
    }

    inicializar_memoria(mem, n);

    int semid = semget(CLAVE_SEMAFORO, 1, IPC_CREAT | 0666);
    if (semid == -1) {
        perror("semget");
        shmdt(mem);
        exit(1);
    }

    if (semctl(semid, 0, SETVAL, 1) == -1) {
        perror("Error al obtener semáforo");
         shmctl(shmid, IPC_RMID, NULL);  // Elimina la memoria si hay un fallo con semáforo
        shmdt(mem);
        exit(1);
    }

    registrar_evento("Simulación iniciada", getpid(), 0, n - 1);

    printf("Inicialización completa.\n");

    shmdt(mem);
    return 0;
}

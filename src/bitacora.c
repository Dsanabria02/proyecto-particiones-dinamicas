#include "../include/bitacora.h"

void registrar_evento(const char *accion, pid_t pid, int inicio, int fin) {
    FILE *f = fopen("bitacora.txt", "a");
    if (!f) return;

    time_t t = time(NULL);
    struct tm *info = localtime(&t);

    fprintf(f, "[%02d:%02d:%02d] PID %d → %s (líneas %d a %d)\n",
            info->tm_hour, info->tm_min, info->tm_sec,
            pid, accion, inicio, fin);

    fclose(f);
}

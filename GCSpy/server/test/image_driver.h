#ifndef _IMAGE_DRIVER_H_
#define _IMAGE_DRIVER_H_

#include "gcspy_gc_driver.h"
#include "gcspy_d_utils.h"

#define IMAGE_USED_SPACE_STREAM     0
#define IMAGE_ROOTS_STREAM          1

typedef struct {
  int usedSpace;
  int roots;
} image_driver_tile_t;

typedef struct {
  gcspy_gc_driver_t *driver;
  gcspy_d_utils_area_t area;
  int totalUsedSpace[2];
  int totalRoots;
} image_driver_t;

void imageDriverInit(image_driver_t *driver, gcspy_gc_driver_t *gcDriver,
                     const char *name, unsigned blockSize, char *start, char *end);
void imageDriverZero(image_driver_t *driver, char *limit);
void imageDriverSetEnd(image_driver_t *driver, char *end);
void imageDriverRoot(image_driver_t *driver, char *addr);
void imageDriverSend(image_driver_t *driver, unsigned event);

#endif /* _IMAGE_DRIVER_H_ */

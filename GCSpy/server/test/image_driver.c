#include <stdio.h>
#include <stdint.h>
#include <stddef.h>
#include <stdlib.h>
#include "gcspy_gc_stream.h"
#include "image_driver.h"

static image_driver_tile_t *
imageDriverAllocateStats (int tileNum) {
  int len = tileNum * sizeof(image_driver_tile_t);
  image_driver_tile_t *tiles = (image_driver_tile_t *) malloc(len);
  if (tiles == NULL) {
    char buffer[256];
    sprintf(buffer, "ImageSpace: could not allocate %d bytes for tile data", len);
    gcspy_raise_error(buffer);
  }
  return tiles;
}

static void
imageDriverSetupTileNames (image_driver_t *driver,
                           int from,
                           int to) {
  char tmp[256];
  for (int i = from; i < to; ++i) {
    gcspy_dUtilsRangeString(&(driver->area), i, tmp);
    gcspy_driverSetTileName(driver->driver, i, tmp);
  }
}

static image_driver_tile_t *
imageDriverGetTile (image_driver_t *driver, int index) {
  return (image_driver_tile_t *)
      gcspy_d_utils_get_stats_struct(&(driver->area), index, 0);
}

static int
imageDriverGetTileIndex (image_driver_t *driver, char *addr) {
  return gcspy_d_utils_get_index(&(driver->area), addr);
}

static char *
imageDriverGetTileAddr (image_driver_t *driver, int index) {
  return gcspy_d_utils_get_addr(&(driver->area), index);
}

void imageDriverInit(image_driver_t *driver, gcspy_gc_driver_t *gcDriver,
                     const char *name, unsigned blockSize, char *start, char *end)
{
  char tmp[256];
  gcspy_gc_stream_t *stream;
  int tileNum = gcspy_dUtilsTileNum(start, end, blockSize);
  image_driver_tile_t *tiles = imageDriverAllocateStats(tileNum);

  driver->driver = gcDriver;
  gcspy_dUtilsInit(&(driver->area),
                   start, end,
                   0, blockSize, tileNum,
                   (char *) tiles, sizeof(image_driver_tile_t));

  if (blockSize < 1024)
    sprintf(tmp, "Block Size: %d bytes\n", blockSize);
  else
    sprintf(tmp, "Block Size: %dK\n", (blockSize / 1024));
  gcspy_driverInit(gcDriver, -1, name, "Non-moving Space",
                   "Block ", tmp, tileNum, NULL, 0);
  imageDriverSetupTileNames(driver, 0, tileNum);

  stream = gcspy_driverAddStream(gcDriver, IMAGE_USED_SPACE_STREAM);
  gcspy_streamInit(stream, IMAGE_USED_SPACE_STREAM,
                   GCSPY_GC_STREAM_INT_TYPE,
                   "Used Space",
                   0, blockSize,
                   0, 0,
                   "Used Space: ", " bytes",
                   GCSPY_GC_STREAM_PRESENTATION_PERCENT,
                   GCSPY_GC_STREAM_PAINT_STYLE_ZERO, 0,
                   gcspy_colorDBGetColorWithName("Blue"));

  stream = gcspy_driverAddStream(gcDriver, IMAGE_ROOTS_STREAM);
  gcspy_streamInit(stream, IMAGE_ROOTS_STREAM,
                   GCSPY_GC_STREAM_SHORT_TYPE,
                   "Roots",
                   0, gcspy_d_utils_roots_per_block(blockSize),
                   0, 0,
                   "Roots: ", "",
                   GCSPY_GC_STREAM_PRESENTATION_PLUS,
                   GCSPY_GC_STREAM_PAINT_STYLE_ZERO, 0,
                   gcspy_colorDBGetColorWithName("Green"));

  /* finished init */
}

void imageDriverZero(image_driver_t *driver, char *limit)
{
  image_driver_tile_t *tile;
  int totalSpace = limit - driver->area.start;

  if (limit != driver->area.end) {
    int tileNum = gcspy_dUtilsTileNum(driver->area.start, limit,
                                      driver->area.blockSize);
    driver->area.end = limit;
    driver->area.blockNum = tileNum;
    free(driver->area.stats);
    driver->area.stats = (char *) imageDriverAllocateStats(tileNum);
    gcspy_driverResize(driver->driver, tileNum);
    imageDriverSetupTileNames(driver, 0, tileNum);
  }

  for (int i = 0; i < driver->area.blockNum; ++i) {
    tile = imageDriverGetTile(driver, i);
    tile->usedSpace = 0;
    tile->roots = 0;
  }

  driver->totalUsedSpace[0] = 0;
  driver->totalUsedSpace[1] = totalSpace;
  driver->totalRoots = 0;
}

void imageDriverSetEnd(image_driver_t *driver, char *end)
{
  driver->totalUsedSpace[0] = (end - driver->area.start);
  gcspy_dUtilsSetPerc(&(driver->area),
                      driver->area.start,
                      end,
                      offsetof(image_driver_tile_t, usedSpace));
}

void imageDriverRoot(image_driver_t *driver, char *addr)
{
  ++driver->totalRoots;
  gcspy_dUtilsAddSingle(&(driver->area), addr,
                        offsetof(image_driver_tile_t, roots));
}

void imageDriverSend(image_driver_t *idriver, unsigned event)
{
  image_driver_tile_t *tile;
  gcspy_gc_driver_t *driver = idriver->driver;
  int tileNum = idriver->area.blockNum;
  double perc;
  char tmp[128];
  int size;

  gcspy_driverStartComm(driver);

  gcspy_driverStream(driver, IMAGE_USED_SPACE_STREAM, tileNum);
  for (int i = 0; i < tileNum; ++i) {
    tile = imageDriverGetTile(idriver, i);
    gcspy_driverStreamIntValue(driver, tile->usedSpace);
  }
  gcspy_driverStreamEnd(driver);

  gcspy_driverSummary(driver, IMAGE_USED_SPACE_STREAM, 2);
  gcspy_driverSummaryValue(driver, idriver->totalUsedSpace[0]);
  gcspy_driverSummaryValue(driver, idriver->totalUsedSpace[1]);
  gcspy_driverSummaryEnd(driver);

  gcspy_driverStream(driver, IMAGE_ROOTS_STREAM, tileNum);
  for (int i = 0; i < tileNum; ++i) {
    tile = imageDriverGetTile(idriver, i);
    gcspy_driverStreamShortValue(driver, tile->roots);
  }
  gcspy_driverStreamEnd(driver);

  gcspy_driverSummary(driver, IMAGE_ROOTS_STREAM, 1);
  gcspy_driverSummaryValue(driver, idriver->totalRoots);
  gcspy_driverSummaryEnd(driver);

  size = idriver->area.end - idriver->area.start;
  sprintf(tmp, "Current Size: %s\n", gcspy_formatSize(size));
  gcspy_driverSpaceInfo(driver, tmp);

  gcspy_driverEndComm(driver);
}

type compare<T> = (a: T, b: T) => number;

export function compare<T>(a: T, b: T): number {
  if (a == null || b == null) {
    return a == b ? 0 : a == null ? 1 : -1;
  }
  return a == b ? 0 : a < b ? -1 : 1;
}

export function comparatorKey<T>(...keys: string[]): compare<T> {
  return function <T>(a: T, b: T): number {
    for (let key of keys) {
      const c = compare(a && a[key], b && b[key]);
      if (c !== 0) {
        return c;
      }
    }
    return 0;
  };
}

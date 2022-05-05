type compareFunc<T> = (a: T, b: T) => number;

export function compare<T>(a: T, b: T): number {
  if (a == null && b == null) {
    return 0;
  }

  if (a == null || b == null) {
    return a == null ? 1 : -1;
  }

  if (a === b) {
    return 0;
  }

  return a < b ? -1 : 1;
}

export function comparatorKey<T>(...keys: string[]): compareFunc<T> {
  return function <T>(a: T, b: T): number {
    for (const key of keys) {
      const c = compare(a && a[key], b && b[key]);
      if (c !== 0) {
        return c;
      }
    }
    return 0;
  };
}

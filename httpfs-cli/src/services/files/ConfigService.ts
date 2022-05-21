const baseUrl = document.getElementsByTagName('base')[0]?.getAttribute('href');
const apiEndpoint = `${baseUrl?.replace(/\/$/, '')}/api`;

const config = {
  sync: false,
  baseUrl,
  apiEndpoint,
  configApiEndpont: `${apiEndpoint}/config`,
  filesApiEndpoint: `${apiEndpoint}/files`,
  maxFileSize: Number.MAX_VALUE,
  maxRequestSize: Number.MAX_VALUE,
};

export function getConfig() {
  return { ...config };
}

export async function configAsync() {
  if (!config.sync) {
    const resConfig = await fetch(config.configApiEndpont).then(res =>
      res.json()
    );
    Object.assign(config, resConfig);
    config.sync = true;
  }
  return { ...config };
}

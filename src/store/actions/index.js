import api from "../../api/api";

export const fetchProducts = () => async (dispatch) => {
  try {
    const { data } = await api.get(`/public/products`);
    console.log(data.response);
    dispatch({
      type: "FETCH_PRODUCTS",
      payload: data.response,
      pageNumber: data.pageNumber,
      pageSize: data.pageSize,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      lastPage: data.lastPage,
    });
  } catch (error) {
    console.log(error);
  }
};

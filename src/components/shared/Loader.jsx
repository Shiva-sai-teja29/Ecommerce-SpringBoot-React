const Loader = ({text}) => {
    return (
        <div className="flex justify-center items-center w-full h-[450px]">
            <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
            <p>{text ? text : "Please wait..."}</p>
        </div>
    );
};

export default Loader;
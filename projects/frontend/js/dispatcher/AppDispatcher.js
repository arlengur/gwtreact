import {Dispatcher} from 'flux';

var dispatcher = new Dispatcher();

export default dispatcher;

export function action(payload) {
    dispatcher.dispatch(payload);
}